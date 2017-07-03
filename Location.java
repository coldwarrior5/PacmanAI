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

/**
 *
 * @author dean
 */
public class Location implements Comparable<Location>
{
    int x=0;
    int y=0;

    Location(int x, int y)
    {this.x=x; this.y=y;}

    int getX() {return x;}
    int getY() {return y;}

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Location)
        {
            Location temp = (Location) o;
            if ((temp.x==this.x) && (temp.y==this.y))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    }

    public float distanceTo(Location other)
    {
        int distanceX = other.x - x;
        int distanceY = other.y - y;

        return (float) Math.abs(distanceX) + Math.abs(distanceY);
//            return (float) Math.sqrt(distanceX*distanceX + distanceY+distanceY);
    }

    @Override
    public int compareTo(Location o) {
        if (x==o.x)
        {
            return Integer.compare(y, o.y);
        }
        else
        {
            return Integer.compare(x, o.x);
        }
    }
}