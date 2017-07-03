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
import AITest.Cell.AStarCellType;

public class AStar 
{
    public class Cell
    {  
        float moveCost = 1;         //How hard is terrain
        float heuristicCost = 0;    //Heuristic cost
        float length = 0;           //Length
        float finalCost = 0;        //Length + Heuristic
        
        Location location;
        AStarCellType type = AStarCellType.EMPTY;
        
        Cell parent; 
        
        Cell(int i, int j)
        {
            this.parent = null;
            this.location = new Location(i, j);
        }
        
        @Override
        public String toString()
        {
            return "["+this.location.x+", "+this.location.y+"]";
        }
        
        public Cell Copy()
        {
            Cell temp = new Cell(this.location.x, this.location.y);
            temp.finalCost = this.finalCost;
            temp.heuristicCost = this.heuristicCost;
            temp.length = this.length;
            temp.moveCost = this.moveCost;
            temp.parent = this.parent;
            return temp;
        }
    }
    
    //Blocked cells are just null Cell values in grid
    int width;
    int height;
    Cell [][] grid;   
    PriorityQueue<Cell> open;
    Cell startCell;
    Cell endCell;
            
    private void setBlocked(Location location)
    {
        this.grid[location.x][location.y].type = AStarCellType.WALL;
    }
    
    private void setStartCell(Location location)
    {
        this.grid[location.x][location.y].type = AStarCellType.START;
        this.startCell = this.grid[location.x][location.y];
        this.open.add(startCell);
    }
    
    private void setEndCell(Location location)
    {
        this.grid[location.x][location.y].type = AStarCellType.END;
        this.endCell = grid[location.x][location.y];
    }
    
    public AStar(int width, int height, Location start, int[][] map)
    {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.open = new PriorityQueue<>((Object o1, Object o2) -> {
             Cell c1 = (Cell)o1;
             Cell c2 = (Cell)o2;

             return (c1.finalCost < c2.finalCost) ? -1 : (c1.finalCost > c2.finalCost) ? 1 : 0;
         });

        for(int i = 0; i < width; i++)
        {
           for(int j = 0; j < height; j++)
           {
               this.grid[i][j] = new Cell(i, j);
               if(map[i][j] == AStarCellType.END.value)
                   setEndCell(new Location(i, j));
               else if(map[i][j] == AStarCellType.WALL.value)
                   setBlocked(new Location(i, j));
           }
        }
        
        setStartCell(start);
    }
    
    public int[] Search()
    {   
        Cell best = new Cell(0, 0);
        best.finalCost = Float.MAX_VALUE;
        Cell current;
        ArrayList<Location> visited = new ArrayList<Location>();
        visited.add(startCell.location);
        
        while(!open.isEmpty())
        {
            current = open.poll();

            if(current.type.equals(AStarCellType.END))
            {
                best = current;
                break;
            }
            
            ArrayList<Cell> neighbors = GetNeighbors(current);
            
            for (int i = 0; i < neighbors.size(); i++) 
            {
                Cell next = neighbors.get(i);
                float length = next.moveCost + current.length;
                boolean wasVisited = visited.contains(next.location);
                
                if(!wasVisited || length < next.length)
                {
                    next.length = length;
                    next.heuristicCost = ManhattanDistance(next.location, endCell.location);
                    next.finalCost = next.length + next.heuristicCost;
                    next.parent = current;
                    if(next.finalCost <= best.finalCost)
                        best = next;
                    visited.add(next.location);
                    open.add(next);
                }
            }
        }
        
        Cell bestParent = best.Copy();
        int iter = 0;
        int maxIter = 20;
        
        while(best.parent != null && iter++ < maxIter)
        {
            bestParent = best;
            best = best.parent;
        }
        
        int[] move = new int[]{ bestParent.location.x - best.location.x, bestParent.location.y - best.location.y};
        return move;
    }
    
    public ArrayList<Cell> GetNeighbors(Cell input)
    {
        ArrayList<Cell> neighbors = new ArrayList<>();
        if(input.location.x - 1 >= 0 && grid[input.location.x - 1][input.location.y].type != AStarCellType.WALL)
            neighbors.add(grid[input.location.x - 1][input.location.y]);
        if(input.location.y - 1 >= 0 && grid[input.location.x][input.location.y - 1].type != AStarCellType.WALL)
            neighbors.add(grid[input.location.x][input.location.y - 1]);
        if(input.location.x + 1 < width && grid[input.location.x + 1][input.location.y].type != AStarCellType.WALL)
            neighbors.add(grid[input.location.x + 1][input.location.y]);
        if(input.location.y + 1 < height && grid[input.location.x][input.location.y + 1].type != AStarCellType.WALL)
            neighbors.add(grid[input.location.x][input.location.y + 1]);
        return neighbors;
    }
    
    public float ManhattanDistance(Location move, Location pos)
    {
        return Math.abs(pos.x - move.x) + Math.abs(pos.y - move.y);
    }
}