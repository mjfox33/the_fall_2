package org.fall;

import java.util.*;
import java.io.*;
import java.math.*;

class Player {
    public static class Game {
        private enum Rotation {
            None,
            Right,
            Left
        }

        private static class EnterDirection {
            public static final String Top = "TOP";
            public static final String Left = "LEFT";
            public static final String Right = "RIGHT";
        }

        private static class Command {
            public static final String Wait = "WAIT";

            public static String Left(int row, int col) {
                return getRotationCommand(row, col, Rotation.Left);
            }

            public static String Right(int row, int col) {
                return getRotationCommand(row, col, Rotation.Right);
            }

            private static String getRotationCommand(int row, int col, Rotation rotation) {
                String rot = rotation == Rotation.Left ? "LEFT" : "RIGHT";
                return row + " " + col + " " + rot;
            }
        }

        public Game(int width, int height) {
            _width = width;
            _height = height;
            _grid = new Integer[height][width];
        }

        private Integer[][] _grid;

        public Integer[][] getGrid() {
            return _grid;
        }

        public void updateGrid(int row, int col, int val) {
            _grid[row][col] = val;
        }

        private int _width;

        public int getWidth() {
            return _width;
        }

        private int _height;

        public int getHeight() {
            return _height;
        }

        private int _exitCol;

        public void setExitCol(int col) {
            _exitCol = col;
        }

        public int getExitCol() {
            return _exitCol;
        }

        private static final HashMap<Integer, HashSet<Integer>> _cellTypeRotatedTypes = new HashMap<Integer, HashSet<Integer>>() {
            {
                put(2, new HashSet<>(Arrays.asList(2, 3)));
                put(3, new HashSet<>(Arrays.asList(2, 3)));
                put(4, new HashSet<>(Arrays.asList(4, 5)));
                put(5, new HashSet<>(Arrays.asList(4, 5)));
                put(6, new HashSet<>(Arrays.asList(6, 7, 8, 9)));
                put(7, new HashSet<>(Arrays.asList(6, 7, 8, 9)));
                put(8, new HashSet<>(Arrays.asList(6, 7, 8, 9)));
                put(9, new HashSet<>(Arrays.asList(6, 7, 8, 9)));
                put(10, new HashSet<>(Arrays.asList(10, 11, 12, 13)));
                put(11, new HashSet<>(Arrays.asList(10, 11, 12, 13)));
                put(12, new HashSet<>(Arrays.asList(10, 11, 12, 13)));
                put(13, new HashSet<>(Arrays.asList(10, 11, 12, 13)));
            }
        };

        private static final Integer[] _leftToDownSetValues = new Integer[] { 1, -1, 5, -5, 8, -8, 9, -9, 13, -13 };
        private HashSet<Integer> _leftToDown = new HashSet<>(Arrays.asList(_leftToDownSetValues));

        private static final Integer[] _leftToRightSetValues = new Integer[] { 2, -2, 6, -6 };
        private HashSet<Integer> _leftToRight = new HashSet<>(Arrays.asList(_leftToRightSetValues));

        private static final Integer[] _rightToDownSetValues = new Integer[] { 1, -1, 4, -4, 7, -7, 8, -8, 12, -12 };
        private HashSet<Integer> _rightToDown = new HashSet<>(Arrays.asList(_rightToDownSetValues));

        private static final Integer[] _rightToLeftSetValues = new Integer[] { 2, -2, 6, -6 };
        private HashSet<Integer> _rightToLeft = new HashSet<>(Arrays.asList(_rightToLeftSetValues));

        private static final Integer[] _topToDownSetValues = new Integer[] { 1, -1, 3, -3, 7, -7, 9, -9 };
        private HashSet<Integer> _topToDown = new HashSet<>(Arrays.asList(_topToDownSetValues));

        private static final Integer[] _topToRightSetValues = new Integer[] { 5, -5, 11, -11 };
        private HashSet<Integer> _topToRight = new HashSet<>(Arrays.asList(_topToRightSetValues));

        private static final Integer[] _topToLeftSetValues = new Integer[] { 4, -4, 10, -10 };
        private HashSet<Integer> _topToLeft = new HashSet<>(Arrays.asList(_topToLeftSetValues));

        // returns null if this is an invalid row, col, or the cell it is going to
        // doesnt work
        private State getNextState(int row, int col, String entered, int cellType, List<State> traveledPath) {
            String nextEntered;
            if (entered.equals(EnterDirection.Left)) {
                if (_leftToDown.contains(cellType)) {
                    row++;
                    nextEntered = EnterDirection.Top;
                } else if (_leftToRight.contains(cellType)) {
                    col++;
                    nextEntered = EnterDirection.Left;
                } else {
                    return null;
                }
            } else if (entered.equals(EnterDirection.Right)) {
                if (_rightToDown.contains(cellType)) {
                    row++;
                    nextEntered = EnterDirection.Top;
                } else if (_rightToLeft.contains(cellType)) {
                    col--;
                    nextEntered = EnterDirection.Right;
                } else {
                    return null;
                }
            } else if (entered.equals(EnterDirection.Top)) {
                if (_topToDown.contains(cellType)) {
                    row++;
                    nextEntered = EnterDirection.Top;
                } else if (_topToRight.contains(cellType)) {
                    col++;
                    nextEntered = EnterDirection.Left;
                } else if (_topToLeft.contains(cellType)) {
                    col--;
                    nextEntered = EnterDirection.Right;
                } else {
                    return null;
                }
            } else {
                return null;
            }

            if (row < 0 || row > _height - 1 || col < 0 || col > _width - 1) {
                return null;
            }

            return new State(row, col, nextEntered, _grid[row][col], traveledPath);
        }

        private int rotateCell(int cellType, Rotation direction) {
            if (cellType < 2) {
                return cellType;
            }

            if (cellType == 2)
                return 3;
            if (cellType == 3)
                return 2;
            if (cellType == 4)
                return 5;
            if (cellType == 5)
                return 4;

            if (cellType < 10) {
                if (direction == Rotation.Right) {
                    return (cellType + 3) % 4 + 6;
                }
                return (cellType + 5) % 4 + 6;
            }

            if (direction == Rotation.Right) {
                return (cellType + 3) % 4 + 10;
            }

            return (cellType + 5) % 4 + 10;
        }

        private class State implements Comparable<State> {
            public int row;
            public int col;
            public String enterDirection;
            public int cellType;
            public List<State> traveledPath;

            public State(int row, int col, String enterDirection, int cellType, List<State> traveledPath) {
                this.row = row;
                this.col = col;
                this.enterDirection = enterDirection;
                this.cellType = cellType;
                this.traveledPath = new ArrayList<State>(traveledPath);
            }

            public int compareTo(State other) {
                return Comparator.comparing((State s) -> s.enterDirection)
                        .thenComparingInt(s -> s.row)
                        .thenComparingInt(s -> s.col)
                        .thenComparingInt(s -> s.cellType)
                        .compare(this, other);
            }
        }

        private List<String> bfs(int row, int col, String enterDirection) {
            State start = new State(row, col, enterDirection, _grid[row][col], new ArrayList<>());
            Queue<State> worker = new PriorityQueue<>();
            worker.add(start);

            Queue<State> validPaths = new PriorityQueue<>();

            while (worker.size() > 0) {
                State current = worker.poll();
                State next = getNextState(current.row, current.col, current.enterDirection, current.cellType,
                        current.traveledPath);

                if (next == null) {
                    continue;
                }

                next.traveledPath.add(current);
                if (next.row == _height - 1 && next.col == _exitCol) {
                    validPaths.add(next);
                } else {
                    if (next.cellType < 2) {
                        worker.add(next);
                    } else {
                        // queue the other possible rotated cellTypes
                        for (int validCellType : _cellTypeRotatedTypes.get(next.cellType)) {
                            State nextValid = new State(next.row, next.col, next.enterDirection, validCellType,
                                    next.traveledPath);
                            worker.add(nextValid);
                        }
                    }
                }
            }

            State validPath = validPaths.poll();
            List<String> moves = new ArrayList<>();
            for (State curr : validPath.traveledPath) {
                int cellDiff = curr.cellType - _grid[curr.row][curr.col];
                if (cellDiff == 1 || cellDiff == -3) {
                    moves.add(Command.getRotationCommand(curr.row, curr.col, Rotation.Right));
                } else if (cellDiff == -1 || cellDiff == 3) {
                    moves.add(Command.getRotationCommand(curr.row, curr.col, Rotation.Left));
                } else if (cellDiff == 2 || cellDiff == -2) {
                    moves.add(Command.getRotationCommand(curr.row, curr.col, Rotation.Right));
                    moves.add(Command.getRotationCommand(curr.row, curr.col, Rotation.Right));
                }
            }
            return moves;
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int width = in.nextInt(); // number of columns.
        int height = in.nextInt(); // number of rows.
        if (in.hasNextLine()) {
            in.nextLine();
        }

        Game game = new Game(width, height);

        for (int row = 0; row < height; row++) {
            String line = in.nextLine();
            String[] cells = line.split("\\s+");
            for (int col = 0; col < width; col++) {
                game.updateGrid(row, col, Integer.parseInt(cells[col]));
            }
        }

        System.err.println(Arrays.deepToString(game.getGrid()));

        int exit = in.nextInt(); // the coordinate along the X axis of the exit.
        game.setExitCol(exit);

        int loop = 0;
        List<String> moves = new ArrayList<>();

        // game loop
        while (true) {
            int xIndy = in.nextInt();
            int yIndy = in.nextInt();
            String posIndy = in.next(); // indy enters this cell from: TOP, LEFT, or RIGHT

            if (loop == 0) {
                moves = game.bfs(yIndy, xIndy, posIndy);
            }

            int rocks = in.nextInt(); // the number of rocks currently in the grid.
            for (int i = 0; i < rocks; i++) {
                int xRock = in.nextInt();
                int yRock = in.nextInt();
                String POSR = in.next();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // One line containing on of three commands: 'X Y LEFT', 'X Y RIGheightT' or
            // 'WAIT'
            String command = loop < moves.size() ? moves.get(loop) : "WAIT";
            loop++;
            System.out.println(command);// moves[loop++]);
        }
    }
}
