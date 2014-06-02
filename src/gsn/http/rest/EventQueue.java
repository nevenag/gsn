/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2014, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: src/gsn/http/rest/EventQueue.java
*
* Class is used to queue requests from the sensor to post data to the GSN.
* Requests are consumed by RemoteEventPush wrapper. 
*
* @author Ali Salehi
* @author Nevena Golubovic
*
*/

package gsn.http.rest;

import java.util.LinkedList;


public class EventQueue {
	
	private static EventQueue singleton = new EventQueue();
	
	private LinkedList<String> data = new LinkedList<String>();
	
	public static EventQueue getInstance(){
		return singleton;
	}
	
	public boolean isEmpty(){ 
		return data.isEmpty();
	}
	
	public void addData(String data){
		this.data.add(data);
	}
	
	public String removeData(){
		return data.remove();
	}
	
	
}
