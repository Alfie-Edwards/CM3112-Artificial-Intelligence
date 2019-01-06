package players;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.geometry.Pos;

/**
 *
 * @author steven
 */
 class Position {

        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int hashCode() {
            return 1024 * x + y;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Position)) {
                return false;
            }

            Position pos = (Position) o;
            return (x == pos.x && y == pos.y);
        }

        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }