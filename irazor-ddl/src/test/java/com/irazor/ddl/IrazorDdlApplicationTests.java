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
package com.irazor.ddl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.irazor.ddl.dao.DDLMapper;
import com.irazor.ddl.entity.OracleMeta;
import com.irazor.ddl.service.OracleDDLGenService;

/**
 * 
 * @author ericzhong
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IrazorDdlApplicationTests {

	 @Autowired
	 DDLMapper ddlMapper;
	 
	 @Autowired
	 OracleDDLGenService gSrv;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void checkDBConnection() {
		int flag = ddlMapper.testQuery();
		assertEquals(1, flag);
	}
	
	@Test
	public void testFetchOracleDDL() {
		List<OracleMeta> list = ddlMapper.fetchOracleDDL();
		for (OracleMeta meta : list) {
			System.out.println(meta.getTableName());
		}
		System.out.println(String.format("The table count is %d", list.size()));
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testFetchOracleDDLByName() {
		String tableName = "Table1";
		OracleMeta om = ddlMapper.fetchOracleDDLByTableName(tableName);		
		System.out.println(om.getTableName());
		assertEquals(tableName, om.getTableName());
	}
	
	@Test
	public void testFetchOracleDDLByTableNameService() {
		String tableName = "Table1";
		String ddl = gSrv.genOracleDDL(tableName);		
		System.out.println(ddl);
		assertNotNull(ddl);
	}
	
	@Test
	public void testFetchOracleDDLService() {
		String oracleDDL = gSrv.genAllOracleDDL();
		System.out.println(oracleDDL);
		assertNotNull(oracleDDL);
	}
	
	@Test
	public void genOracleDDLFile() throws FileNotFoundException {
		gSrv.genOracleDDLFile(null, null);
	}
	
	@Test
	public void callGenDDLSetting() {
		ddlMapper.callGenDDLSettingPretty();
	}
	
	/**
	 * Main Test Method
	 * @throws FileNotFoundException
	 */
	@Test
	public void genOracleDDLFileByTableName() throws FileNotFoundException {
		gSrv.genOracleDDLFile("Table1", null);
//		gSrv.genOracleDDLFile(null, null);
	}
}
