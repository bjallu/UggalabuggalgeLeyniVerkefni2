import java.util.*;
import java.util.stream.Collectors;

public class Player {
    /**
     *
     *
     * GLOBALS
     *
     *
     */

    private final int MAX_DEPTH = 6;
    private final int[] Xweights = {0,1,10,100,1000};
    private final int[] Oweights = {0,1,9,95,950};




    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        //Random random = new Random();
        //return nextStates.elementAt(random.nextInt(nextStates.size()));

        /*
        Vector<Integer> s = new Vector<>();
        for (GameState n:nextStates){
            s.add(alphabeta(n));
        }
        */

        //return nextStates.elementAt(s.indexOf(Collections.max(s)));
        return alphabeta(gameState);



    }


    /**
     *
     *
     * FUNCTIONS OTHER THAN PLAY
     *
     *
     */

    public class M{
        public GameState gameState;
        public Integer eval;

        public M(GameState gameState, int eval){
            this.eval = eval;
            this.gameState = gameState;
        }

        public Integer getEval(){
            return this.eval;
        }

        public GameState getGameState() {
            return gameState;
        }
    }

    public Vector<GameState> evalSort(Vector<GameState> states){
        Vector<M> mStates = new Vector<>();

        for (GameState g:states){
            mStates.add(new M(g,evaluationFunction(g,g.getNextPlayer())));
        }
        mStates.sort(Comparator.comparing(M::getEval));

        return new Vector<GameState>(mStates.stream().map(M::getGameState).collect(Collectors.toList()));

    }



    public GameState alphabeta(GameState gameState){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depth = MAX_DEPTH;
        int player = gameState.getNextPlayer();
        Vector<Integer> s = new Vector<>();
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        nextStates = evalSort(nextStates);
        int v;

        if (depth==0 || nextStates.isEmpty()){
            v = evaluationFunction(gameState,player);
        } else if (player==Constants.CELL_X){
            v = Integer.MIN_VALUE;
            for (GameState g: nextStates){
                v = Math.max(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_O));
                alpha = Math.max(alpha,v);
                s.add(v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (GameState g: nextStates){
                v = Math.min(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_X));
                beta = Math.min(beta,v);
                s.add(v);
                if (beta <= alpha){
                    break;
                }
            }

        }

        return nextStates.elementAt(s.indexOf(Collections.max(s)));

    }


    public int alphabeta(GameState gameState, int depth, int alpha, int beta, int player){
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        if (depth>0) {
            nextStates = evalSort(nextStates);
        }
        int v;

        if (depth==0 || nextStates.isEmpty()){
            v = evaluationFunction(gameState,player);
        } else if (player==Constants.CELL_X){
            v = Integer.MIN_VALUE;
            for (GameState g: nextStates){
                v = Math.max(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_O));
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (GameState g: nextStates){
                v = Math.min(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_X));
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }
            }

        }
        return v;

    }

    public int evaluationFunction(GameState state, int player){
        if (state.isXWin()){
            return 1000;
        } else if (state.isOWin()){
            return -1000;
        } else if (state.isEOG()){
            return 0;
        }
        int thisStatesScore = 0;
        // Sum all points for rows
        for(int row = 0; row<GameState.BOARD_SIZE; row++){
            thisStatesScore += myMarks(state, row,row,0,GameState.BOARD_SIZE,player);
        }
        // sum all points for columns
        for(int col = 0; col<GameState.BOARD_SIZE; col++){
            thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE,col,col,player);
        }
        // sum all points for the two diagonal lines
        thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,player);
        thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,player);
        //System.err.println("!!!!!!!!!!! This states score !!!!!!!!   " + thisStatesScore );
        return thisStatesScore;
    }

    /*public int myMarks(GameState state, int row, int row2, int col, int col2, int player){
        int dRow = (row2-row) / (GameState.BOARD_SIZE - 1);
        int dCol = (col2-col) / (GameState.BOARD_SIZE - 1);
        int playerScore = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            if (state.at(row + i*dRow,col+i*dCol) == player) {
                playerScore += 1; 
            }
        }
        return playerScore;
    }
    */

    /*
    public int myMarks(GameState state, int row, int row2, int col, int col2, int player){
        int inARow = 0;
        int dRow = (row2-row) / (GameState.BOARD_SIZE - 1);
        int dCol = (col2-col) / (GameState.BOARD_SIZE - 1);
        if (state.at(row,col)==player) inARow++;
        for (int i = 1; i < GameState.BOARD_SIZE; ++i) {
            if (state.at(row + i*dRow,col+i*dCol) == player &&
                    state.at(row + i*dRow,col+i*dCol) == state.at(row + (i-1)*dRow,col+(i-1)*dCol) ) {
                inARow++;
            } else if (state.at(row + i*dRow,col+i*dCol) != player &&
                    state.at(row + i*dRow,col+i*dCol) != Constants.CELL_EMPTY){
                inARow = 0;
                break;
            } else if (state.at(row + i*dRow,col+i*dCol) == player &&
                    inARow > 0 &&
                    state.at(row + i*dRow,col+i*dCol) != state.at(row + (i-1)*dRow,col+(i-1)*dCol)){
                inARow = Math.max(inARow,1);
            }
        }
        return inARow;
    }

    */


    public int myMarks(GameState state, int row, int row2, int col, int col2, int player) {
        int XinARow = 0;
        int OinARow = 0;
        int maxX = 0;
        int maxO = 0;
        int emptySpaces = 0;
        int dRow = (row2 - row) / (GameState.BOARD_SIZE - 1);
        int dCol = (col2 - col) / (GameState.BOARD_SIZE - 1);
        int oldPiece = state.at(row, col);
        if (oldPiece == Constants.CELL_X) {
            XinARow++;
        } else if (oldPiece == Constants.CELL_O) {
            OinARow++;
        } else {
            emptySpaces++;
        }
        for (int i = 1; i < GameState.BOARD_SIZE; i++) {
            int piece = state.at(row + i * dRow, col + i * dCol);
            if (piece == oldPiece) {
                if (piece == Constants.CELL_X) {
                    XinARow++;
                } else if (piece == Constants.CELL_O) {
                    OinARow++;
                } else {
                    emptySpaces++;
                }
            } else if (piece != oldPiece) {
                maxX = Math.max(XinARow, maxX);
                maxO = Math.max(OinARow, maxO);
                XinARow = 0;
                OinARow = 0;
                if (piece == Constants.CELL_EMPTY) {
                    emptySpaces++;
                } else if (piece == Constants.CELL_X) {
                    XinARow++;
                } else {
                    OinARow++;
                }
            }
            oldPiece = piece;
        }
        if (emptySpaces == 0) {
            return 0;
        } else if (maxO*maxX == 0){
            return Xweights[maxX] - Oweights[maxO];
        } else {
            return 0;
        }
    }
}
