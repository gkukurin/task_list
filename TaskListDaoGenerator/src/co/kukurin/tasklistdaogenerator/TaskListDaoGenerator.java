/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.kukurin.tasklistdaogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class TaskListDaoGenerator {

	static final int VERSION = 1;
	
	static Entity tasks;
	
	
    public static void main(String[] args) throws Exception {

    	Schema schema = new Schema(VERSION, "co.kukurin.tasklist.dao");
    	schema.enableKeepSectionsByDefault();
    	
        addTasks(schema);
        
        new DaoGenerator().generateAll(schema, "../TaskList/src-gen");
    }    

    private static void addTasks(Schema schema) {

    	tasks = schema.addEntity("Tasks");
        
    	tasks.addIdProperty().autoincrement();
        
    	tasks.addStringProperty("title").notNull();
    	tasks.addStringProperty("description").notNull();
    	tasks.addDateProperty("date_due");
    	tasks.addDateProperty("date_created").notNull();
    	tasks.addBooleanProperty("completed").notNull();
    	tasks.addByteProperty("priority");
    }
    
}
