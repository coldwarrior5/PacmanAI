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

import java.util.*;
import AITest.Cell.BellmanFordCellType;

public class BellmanFord 
{
    public class BellmanFordCell
    {
        BellmanFordCellType type;
        int[] bestPolicy;
        float currentValue;
        float nextValue;
        
        public BellmanFordCell(BellmanFordCellType type)
        {
            this.type = type;
            this.currentValue = 0;
            this.nextValue = 0;
            this.bestPolicy = null;
        }
    }
    
    BellmanFordCell[][] map;
    int[] myLocation;
    public float discount = 0.9f;
    public float delta = 0.01f;
    public int maxIterations = 50;
    private int width;
    private int height;
    boolean powerUP;
    boolean ghostIsNear;
    boolean otherPoints;

    public BellmanFord(int width, int height, BellmanFordCellType[][] map, boolean powerUP, boolean ghostIsNear, boolean otherPoints)
    {
        this.ghostIsNear = ghostIsNear;
        this.otherPoints = otherPoints;
        this.powerUP = powerUP;
        this.width = width;
        this.height = height;
        this.map = new BellmanFordCell[width][height];
        
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                this.map[x][y] = new BellmanFordCell(map[x][y]);
                if(this.map[x][y].type == BellmanFordCellType.PACMAN)
                    myLocation = new int[]{x, y};
            }
        }
    }
    
    public int[] BestPolicy()
    {
        int iter = 0;
        float biggestChange = Float.MAX_VALUE;
        int[] bestState = new int[2];
        
        while(iter++ < maxIterations && biggestChange > delta)
        {
           UpdateValues();
           biggestChange = 0;
           for(int x = 0; x < width; x++)
            {
                for(int y = 0; y < height; y++)
                {
                    if(this.map[x][y].type == BellmanFordCellType.WALL)
                        continue;

                    UpdateValue(new int[]{x, y});
                    float difference = this.map[x][y].nextValue - this.map[x][y].currentValue;

                    if(difference > biggestChange)
                        biggestChange = Math.abs(difference);
                }
            }
        }
        
        bestState = this.map[myLocation[0]][myLocation[1]].bestPolicy;
        int[] move = new int[]{bestState[0] - myLocation[0], bestState[1] - myLocation[1]};
        return move;
    }
    
    private void UpdateValue(int[] direction)
    {
        ArrayList<int[]> neighbors = GetNeighbors(direction);
        float bestValue = Float.NEGATIVE_INFINITY;
        
        ArrayList<int[]> bestNeighbors = new ArrayList<>();
        float currentReward = Cell.ValueOfBellmanFordCell(this.map[direction[0]][direction[1]].type);//float)this.map[direction[0]][direction[1]].type.value;
        if(powerUP)
        {
            if(this.map[direction[0]][direction[1]].type == BellmanFordCellType.GHOST)
                currentReward = -currentReward;
        }
        if(!(!ghostIsNear && otherPoints))
        {
            //Do nothing
        }
        else
        {
            if(this.map[direction[0]][direction[1]].type == BellmanFordCellType.POWERUP)
                currentReward = -currentReward;
        }
        
        float leastValue = Float.POSITIVE_INFINITY;
        for(int i = 0; i < neighbors.size(); i++)
        {
            int[] current = neighbors.get(i);
            float currentValue = this.map[current[0]][current[1]].currentValue;

            if(currentValue < leastValue)
                leastValue = currentValue;
        }

        for (int i = 0; i < neighbors.size(); i++) 
        {
            int[] current = neighbors.get(i);
            float currentValue = this.map[current[0]][current[1]].currentValue;

            float totalValue = discount * (0.8f * currentValue + 0.2f * leastValue);
            if(totalValue > bestValue)
            {
                bestValue = totalValue;
                bestNeighbors.clear();
                bestNeighbors.add(current);
            }
            else if(totalValue == bestValue)
            {
                bestNeighbors.add(current);
            }
        }
        
        if(bestNeighbors.size() == 1)
            this.map[direction[0]][direction[1]].bestPolicy = bestNeighbors.get(0);
        else
        {
            Random r = new Random();
            int choice = r.nextInt(bestNeighbors.size());
            this.map[direction[0]][direction[1]].bestPolicy = bestNeighbors.get(choice);
        }
        
        if(this.map[direction[0]][direction[1]].type == BellmanFordCellType.GHOST)
        {
            this.map[direction[0]][direction[1]].nextValue = currentReward;
        }
        else
        {
            this.map[direction[0]][direction[1]].nextValue = currentReward + bestValue;
        }
    }
    
    private void UpdateValues()
    {
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                this.map[x][y].currentValue = this.map[x][y].nextValue;
            }
        }
    }
    
    public ArrayList<int[]> GetNeighbors(int[] input)
    {
        ArrayList<int[]> neighbors = new ArrayList<int[]>();
        if(input[0] - 1 >= 0 && this.map[input[0] - 1][input[1]].type != BellmanFordCellType.WALL)
            neighbors.add(new int[]{input[0] - 1, input[1]});
        if(input[1] - 1 >= 0 && this.map[input[0]][input[1] - 1].type != BellmanFordCellType.WALL)
            neighbors.add(new int[]{input[0], input[1] - 1});
        if(input[0] + 1 < width && this.map[input[0] + 1][input[1]].type != BellmanFordCellType.WALL)
            neighbors.add(new int[]{input[0] + 1, input[1]});
        if(input[1] + 1 < height && this.map[input[0]][input[1] + 1].type != BellmanFordCellType.WALL)
            neighbors.add(new int[]{input[0], input[1] + 1});
        return neighbors;
    }
}