/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irazor.ddl.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.irazor.ddl.dao.DDLMapper;
import com.irazor.ddl.entity.OracleMeta;

/**
 * 
 * @author ericzhong
 *
 */
@Service
public class OracleDDLGenService {

	 private static final Logger LOGGER = LoggerFactory.getLogger(OracleDDLGenService.class);
	
	 @Autowired
	 DDLMapper ddlMapper;
	
	/**
	 * generate all tables DDL to string.
	 * @return
	 */
	public String genAllOracleDDL() {
		String ddl = Strings.EMPTY;
		
		List<OracleMeta> list = ddlMapper.fetchOracleDDL();
		if(list != null && (!list.isEmpty())) {
			
			LOGGER.info(String.format("Table Count is %d", list.size()));
			
			StringBuffer sb = new StringBuffer();
			for (OracleMeta om : list) {
				sb.append(om.getDdl());
				sb.append(Strings.LINE_SEPARATOR);
				
				LOGGER.info(om.getTableName());
			}
			
			ddl = sb.toString();
		}
		
		return ddl;
	}
	
	/**
	 * generate specific table DDL to string
	 * @param tableName
	 * @return
	 */
	public String genOracleDDL(String tableName) {
		String ddl = Strings.EMPTY;
		
		if(StringUtils.isEmpty(tableName)) {
			return ddl;
		}
		
		OracleMeta om = ddlMapper.fetchOracleDDLByTableName(tableName.toUpperCase());
		ddl = om.getDdl();
		return ddl;
	}
	
	/**
	 * generate the table DDL to file
	 * @param tableName
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void genOracleDDLFile(String tableName, String fileName) throws FileNotFoundException {
		String ddl = Strings.EMPTY;
		if(StringUtils.isEmpty(fileName)) {
			ResourceBundle bundle = ResourceBundle.getBundle("application");
			fileName = bundle.getString("genFileName");
		}
		
		setDDLSetting();
		
		if(StringUtils.isEmpty(tableName)) {
			ddl = genAllOracleDDL();
		}
		else {
			ddl = genOracleDDL(tableName);
		}
		
		ddl = filterUnusedKeywords(ddl);
		
		File file = new File(fileName);
        PrintStream ps = new PrintStream(new FileOutputStream(file));
		ps.print(ddl);
		ps.append(Strings.LINE_SEPARATOR);
		ps.close();
	}
	
	private void setDDLSetting() {
		ddlMapper.callGenDDLSettingAsAlter();
		ddlMapper.callGenDDLSettingConstraints();
		ddlMapper.callGenDDLSettingPretty();
		ddlMapper.callGenDDLSettingRefConstraints();
		ddlMapper.callGenDDLSettingSegment();
		ddlMapper.callGenDDLSettingStorage();
		ddlMapper.callGenDDLSettingTablespace();
		ddlMapper.callGenDDLSettingTerminator();
	}
	
	private String filterUnusedKeywords(String ddl) {
		if(StringUtils.isEmpty(ddl)) {
			return ddl;
		}
		
		ddl = ddl.replace("NUMBER(*,0)", "NUMBER(38,0)");
		ddl = ddl.replace(" ENABLE", "");
		ddl = ddl.replaceAll("USING[^\n]+", "");
		ddl = ddl.replaceAll("CREATE TABLE [\\s\\w\"]+.", "CREATE TABLE ");
		ddl = ddl.replaceAll("SUPPLEMENTAL[^\n]+", "");
		ddl = ddl.replaceAll("[\\s]+,[^\n]+", "");
		ddl = ddl.replaceAll("UNIQUE[^\n]+", "");
		
		//TODO: beta
		//ddl = ddl.replace("NUMBER,", "NUMBER(38,4)");
		//ddl = ddl.replace("NUMBER ", "NUMBER(38,4)");
		
		return ddl;
	}
}
