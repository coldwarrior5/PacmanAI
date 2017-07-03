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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dean
 */
public class Cell 
{
    public enum AStarCellType 
    {
        END(2), START(1), EMPTY(0), WALL(3);

        public final int value;
        private AStarCellType(int value) 
        {
            this.value = value;
        }
    }
    
    public enum BellmanFordCellType 
    {
        EMPTY(0), WALL(0), PACMAN(0), POINT(10), POWERUP(50), GHOST(-200);

        public final int value;
        
        private static Map<Integer, BellmanFordCellType> map = new HashMap<Integer, BellmanFordCellType>();

        static {
            for (BellmanFordCellType valueEnum : BellmanFordCellType.values()) {
                map.put(valueEnum.value, valueEnum);
            }
        }
        private BellmanFordCellType(final int value) 
        {
            this.value = value;
        }
        public static BellmanFordCellType from(int value)
        {
            return map.get(value);
        }
    }
    
    public static int ValueOfBellmanFordCell(BellmanFordCellType type)
    {
        if(null == type)
            return 0;
        
        else switch (type) {
            case EMPTY:
                return BellmanFordCellType.EMPTY.value;
            case WALL:
                return BellmanFordCellType.WALL.value;
            case PACMAN:
                return BellmanFordCellType.PACMAN.value;
            case POINT:
                return BellmanFordCellType.POINT.value;
            case POWERUP:
                return BellmanFordCellType.POWERUP.value;
            case GHOST:
                return BellmanFordCellType.GHOST.value;
            default:
                return 0;
        }
    }
}