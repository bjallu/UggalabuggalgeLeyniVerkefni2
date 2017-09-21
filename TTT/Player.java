import java.util.*;

public class Player {
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

        Random random = new Random();
        return nextStates.elementAt(random.nextInt(nextStates.size()));
    }   
    
    public int alphabeta(GameState gameState, int depth, int alpha, int beta, int player){
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
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
                alpha = Math.min(alpha,v);
                if (beta <= alpha){
                    break;
                }
            }

        }
        return v;

    }

    public int evaluationFunction(GameState state, int player){
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
        return thisStatesScore;
    }

    public int myMarks(GameState state, int row, int row2, int col, int col2, int player){
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


}
