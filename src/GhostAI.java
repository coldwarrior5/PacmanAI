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

import AITest.Cell.AStarCellType;
import java.util.*;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

public class GhostAI extends mmaracic.gameaiframework.AgentAI
{   
    private int id;
    private Location myLocation = new Location(0, 0);
    private Location targetLocation = new Location(0, 0);
    private long now = System.nanoTime();
    private long second = 1000000000;
    private long coolDown = 2 * second;
    private long selfCoolDown = 1 * second;
    
    AStar searchAlgorithm;
    
    public class closeMeta
    {
        private HashMap<Integer, Object> hash;
        private HashMap<Integer, HashMap<Integer, Object>> meta;

        public closeMeta() {
            this.hash = new HashMap<Integer, Object>();
            this.meta = new HashMap<Integer, HashMap<Integer, Object>>();
        }
        
        public void addMeta(int locationId, HashMap<Integer, Object> info)
        {
            this.meta.put(locationId, info);
        }
        
        public HashMap<Integer, Object> getMeta(int locationId)
        {
            if(this.meta.containsKey(locationId))
            {
                return this.meta.get(locationId);
            }
            return null;
        }
        
        public int FindBestOption()
        {
            if(this.meta.size() == 1)
                return 0;
            int nextSpot = FindClear();
            if(nextSpot != -1)
                return nextSpot;
            nextSpot = FindMyOldest();
            if(nextSpot != -1)
                return nextSpot;
            nextSpot = FindOtherOldest();
            if(nextSpot != -1)
                return nextSpot;
            return -1;
        }
        
        private int FindClear()
        {
            for(int i = 0; i < this.meta.size(); i++)
            {
                this.hash = this.meta.get(i);
                if(this.hash.isEmpty())
                    return i;
            }
            return -1;
        }
        
        private int FindMyOldest()
        {
            long biggestDifference = -Long.MAX_VALUE;
            int oldest = -1;
            
            for(int i = 0; i < this.meta.size(); i++)
            {
                this.hash = this.meta.get(i);
                if(this.hash.containsKey(id))
                {
                    now = System.nanoTime();
                    long difference = now - (long)this.hash.get(id);
                    if(difference > selfCoolDown && difference > biggestDifference)
                    {
                        biggestDifference = difference;
                        oldest = i;
                    }
                }
                else
                {
                    biggestDifference = Long.MAX_VALUE;
                    oldest = i;
                }
            }
            return oldest;
        }
        
        private int FindOtherOldest()
        {
            long biggestDifference = -Long.MAX_VALUE;
            int oldest = -1;
            for(int i = 0; i < this.meta.size(); i++)
            {
                this.hash = this.meta.get(i);
                for (Integer currentId : this.hash.keySet())
                {
                    if(currentId == id)
                        continue;
                    
                    now = System.nanoTime();
                    long difference = now - (long)this.hash.get(currentId);
                    if(difference > selfCoolDown && difference > biggestDifference)
                    {
                        biggestDifference = difference;
                        oldest = i;
                    }
                }
            }
            return oldest;
        }
    }
    
    private int ascertainDirection(ArrayList<int[]> moves, Location pos, boolean stateScared)
    {
        int index = 0;
        int[] move = !stateScared ? searchAlgorithm.Search() : moves.get(0);
        if(stateScared)
        {
            float dist = ManhattanDistance(move, pos);

            for(int i = 1; i < moves.size(); i++)
            {
                move = moves.get(i);
                float currDist = ManhattanDistance(move, pos);

                if(currDist > dist)
                {
                    dist = currDist;
                    index = i;
                }
            }
        }
        else
        {
            index = indexOfValue(moves, move);
        }
        return index;
    }
    
    public float ManhattanDistance(int[] move, Location pos)
    {
        return Math.abs(pos.x - move[0]) + Math.abs(pos.y - move[1]);
    }
    
    
    @Override
    public int decideMove(ArrayList<int []> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo)
    {   
        int width = mySurroundings.getDimensionX();
        int height =  mySurroundings.getDimensionY();
        int radiusX = width/2;
        int radiusY = height/2;
        id = myInfo.getID();
        
        boolean found = false;
        boolean somebodyFound = false;
        boolean powerUp = false;    
        int[] move = null;
        int [][] map = new int [width][height];
        float closest = Float.MAX_VALUE;
        Location algLocation = new Location(radiusX, radiusY);
        closeMeta nearMeta = new closeMeta();
            
        for (int i = -radiusX; i <= radiusX; i++)
        {
            for (int j = -radiusY; j <= radiusY; j++)
            {
                int[] location = new int[]{i, j};
                
                if (myLocation.x == i && myLocation.y == j)   //This is me
                    continue;
                //find pacman
                ArrayList<WorldEntity.WorldEntityInfo> elements = mySurroundings.getWorldInfoAt(i, j);
                HashMap<Integer,Object> metaHash = mySurroundings.getWorldMetadataAt(i, j);
                
                if (elements != null)
                {                    
                    for(WorldEntity.WorldEntityInfo el : elements)
                    {
                        if (el.getIdentifier().compareToIgnoreCase("Pacman") == 0)
                        {
                            powerUp = el.hasProperty(mmaracic.gameaiframework.PacmanAgent.powerupPropertyName);
                            printStatus("id: "+ id+ " I found pacman!");
                            found = true;
                            targetLocation.x = i;
                            targetLocation.y = j;
                        }
                        else if (el.getIdentifier().compareToIgnoreCase("Wall") == 0)
                        {
                            map[radiusX + i][radiusY + j] = AStarCellType.WALL.value;
                        }
                        else if (el.getIdentifier().compareToIgnoreCase("Point") == 0   ||
                                 el.getIdentifier().compareToIgnoreCase("Powerup") == 0 ||
                                 el.getIdentifier().compareToIgnoreCase("Ghost") == 0)
                        {
                            map[radiusX + i][radiusY + j] = AStarCellType.EMPTY.value;
                        }
                    }
                    
                    if(ManhattanDistance(location, myLocation) == 1 && containsValue(moves, location))
                    {
                        int locationId = indexOfValue(moves, location);
                        nearMeta.addMeta(locationId, metaHash);
                    }
                }
                
                if (!found && elements != null && metaHash != null && !metaHash.isEmpty())
                {
                    Iterator<Integer> it = metaHash.keySet().iterator();
                    while (it.hasNext()) 
                    {
                        Integer currentId = it.next();
                        if (currentId != id)
                        {
                            now = System.nanoTime();
                            if((long)metaHash.get(currentId) < 0l && now + (long)metaHash.get(currentId) < coolDown)
                            {
                                printStatus("id: "+ id+ " Ghost: " + currentId + " has found pacman!");
                                float distance = ManhattanDistance(location, myLocation);
                                if(distance < closest)
                                {
                                    somebodyFound = true;
                                    closest = distance;
                                    targetLocation.x = i;
                                    targetLocation.y = j;
                                }
                            }
                            else if((long)metaHash.get(currentId) < 0l)
                            {
                                printStatus("id: "+ id+ " Ghost: " + currentId + " has found pacman, but that was long time ago");
                                metaHash.remove(currentId);
                            }
                        }
                    }
                }
                
            }
        }
        
        if(found || somebodyFound)
        {
            map[radiusX + targetLocation.x][radiusY + targetLocation.y] = AStarCellType.END.value;
            searchAlgorithm = new AStar(width, height, algLocation, map);
            int index = ascertainDirection(moves, targetLocation, powerUp);
            move = moves.get(index);
            
            if(!powerUp && found)
            {
                HashMap<Integer,Object> metaHash = mySurroundings.getWorldMetadataAt(move[0], move[1]);
                metaHash.remove(id);
                now = System.nanoTime();
                metaHash.put(id, -now);
            }
            return index;
        }
        
        int choice = nearMeta.FindBestOption();
        //printStatus("BestOption: id: "+ id + ", " + choice);
        if(choice != -1)
        {
            move = (int[]) moves.get(choice);
            HashMap<Integer,Object> metaHash = mySurroundings.getWorldMetadataAt(move[0], move[1]);
            metaHash.remove(id);
            now = System.nanoTime();
            metaHash.put(id, now);
        }
        
        return choice;
    }
    
    private boolean containsValue(ArrayList<int[]> list, int[] element)
    {
        for (int i = 0; i < list.size(); i++) 
        {
            if(Arrays.equals(list.get(i), element))
                return true;
        }
        return false;
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