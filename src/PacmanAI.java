/*
 * Copyright 2017 Dean and Franjo.
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
package AITest;

import AITest.Cell.BellmanFordCellType;
import java.util.ArrayList;
import java.util.Arrays;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

public class PacmanAI extends mmaracic.gameaiframework.AgentAI
{   
    BellmanFord optimalPolicySearch;
    
    @Override
    public int decideMove(ArrayList<int []> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo)
    {
        int width = mySurroundings.getDimensionX();
        int height = mySurroundings.getDimensionY();
        int radiusX = width/2;
        int radiusY = height/2;
               
        boolean powerUP = myInfo.hasProperty(mmaracic.gameaiframework.PacmanAgent.powerupPropertyName);
        boolean ghostIsNear = false;
        boolean otherPoints = false;
        
        Location mapLocation = new Location(radiusX, radiusY);
        BellmanFordCellType[][] map = new BellmanFordCellType[width][height];
        int[][] visits = new int[width][height];
        
        for (int i = -radiusX; i<=radiusX; i++)
        {
            for (int j = -radiusY; j<=radiusY; j++)
            {   
                ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
                
                if (neighPosInfos != null)
                {
                    for (WorldEntity.WorldEntityInfo info : neighPosInfos)
                    {
                        if (info.getIdentifier().compareToIgnoreCase("Pacman")==0)
                            map[mapLocation.x + i][mapLocation.y + j] = BellmanFordCellType.PACMAN;
                        else if (info.getIdentifier().compareToIgnoreCase("Wall")==0)
                            map[mapLocation.x + i][mapLocation.y + j] = BellmanFordCellType.WALL;
                        else if (info.getIdentifier().compareToIgnoreCase("Point")==0)
                        {
                            otherPoints = true;
                            map[mapLocation.x + i][mapLocation.y + j] = BellmanFordCellType.POINT;
                        }
                        else if(info.getIdentifier().compareToIgnoreCase("Powerup")==0)
                            map[mapLocation.x + i][mapLocation.y + j] = BellmanFordCellType.POWERUP;
                        else if (info.getIdentifier().compareToIgnoreCase("Ghost")==0)
                        {
                            ghostIsNear = true;
                            map[mapLocation.x + i][mapLocation.y + j] = BellmanFordCellType.GHOST;
                        }
                        else
                            printStatus("I dont know what "+info.getIdentifier()+" is!");
                    }
                }
            }            
        }
        optimalPolicySearch = new BellmanFord(width, height, map, powerUP, ghostIsNear, otherPoints);
        
        int[] move;
        do{
            move = optimalPolicySearch.BestPolicy();
        }
        while(visits[mapLocation.x + move[0]][mapLocation.y + move[1]] > 3);
        
        visits[mapLocation.x + move[0]][mapLocation.y + move[1]]++;
        int moveIndex = indexOfValue(moves, move);
        return moveIndex;
    }       
    
    private int indexOfValue(ArrayList<int[]> list, int[] element)
    {
        for (int i = 0; i < list.size(); i++) 
        {
            if(Arrays.equals(list.get(i), element))
                return i;
        }
        return 0;
    }
}