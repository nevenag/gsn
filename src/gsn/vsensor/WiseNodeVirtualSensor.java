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
* File: src/gsn/vsensor/WiseNodeVirtualSensor.java
*
* @author Ali Salehi
* @author Mehdi Riahi
*
*/

package gsn.vsensor;

import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class WiseNodeVirtualSensor extends AbstractVirtualSensor {
   
   private static final transient Logger logger = Logger.getLogger( WiseNodeVirtualSensor.class );
   
   public boolean initialize ( ) {
      return true;
      /*
       * Collection<KeyValue> params =
       * virtualSensorConfiguration.getMainClassInitialParams (); for ( KeyValue
       * param : params ) { if ( ( ( String ) param.getKey () ).trim
       * ().equalsIgnoreCase ( "KEYWORD" ) ) { ChartInfo chartInfo = new
       * ChartInfo ( ( String ) param.getValue () ); } }
       */
   }
   
   public void dataAvailable ( String inputStreamName , StreamElement data ) {
      dataProduced( data );
      logger.info( "Data received under the name: " + inputStreamName );
      
      /**
       * Creating the stream element(s) for output. For creating a stream
       * element one need to provide the field names (in the form of string
       * array) and their types (in the form of integer array). Here we extract
       * temperature readings and node addresses, so we have two integer values
       * to output.
       */
      
      String [ ] fieldNames = new String [ ] { "Node" , "Temperature" };
      Byte [ ] fieldTypes = new Byte[]{ DataTypes.INTEGER, DataTypes.INTEGER};
      Serializable [ ] outputData = new Serializable [ fieldNames.length ];
      
      byte [ ] buffer = ( byte [ ] ) data.getData( "RAW_PACKET" );
      
      if ( buffer[ 0 ] == 'e' && buffer[ 1 ] == 'd' && buffer[ 3 ] == 's' && buffer[ 4 ] == 18 ) {
         
         int temperature = buffer[ 19 ];
         int node = buffer[ 4 ] * 16 + buffer[ 5 ];
         outputData[ 0 ] = node;
         outputData[ 1 ] = temperature;
         /**
          * Creating a stream element with the specified fieldnames,
          * fieldtypes,data output and using the current time as the timestamp
          * of the stream element.
          */
         
         StreamElement output = new StreamElement( fieldNames , fieldTypes , outputData , System.currentTimeMillis( ) );
         
         /**
          * Informing container about existance of a stream element.
          */
         dataProduced( output );
      }
   }
   
   public void dispose ( ) {

   }
   
}
