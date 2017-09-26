import javax.swing.*;
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

    private final int MAX_DEPTH = 8;
    private final int MAN_VALUE = 5;
    private final int KING_VALUE = 15;
    private final int BOARD_SIZE = 8; // 8 COLS X 8 ROWS



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
            mStates.add(new M(g,evaluationFunction(g)));
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
            v = evaluationFunction(gameState);
        } else if (player==Constants.CELL_RED){
            v = Integer.MIN_VALUE;
            for (GameState g: nextStates){
                v = Math.max(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_WHITE));
                alpha = Math.max(alpha,v);
                s.add(v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (GameState g: nextStates){
                v = Math.min(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_RED));
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
            v = evaluationFunction(gameState);
        } else if (player==Constants.CELL_RED){
            v = Integer.MIN_VALUE;
            for (GameState g: nextStates){
                v = Math.max(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_WHITE));
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (GameState g: nextStates){
                v = Math.min(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_RED));
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }
            }

        }
        return v;

    }

    public int evaluationFunction(GameState gameState){

        int result = 0;
        for (int r = 0; r < BOARD_SIZE; r++){
            for (int c = 0; c < BOARD_SIZE/2; c++){
                int cOffset = (r+1)%2;
                int piece = gameState.get(r,cOffset+2*c);
                int pieceVal = (piece & Constants.CELL_KING) == 4 ? KING_VALUE: MAN_VALUE;
                if (piece == Constants.CELL_WHITE){
                    //result--;
                    result -= pieceVal+(BOARD_SIZE-r-1);
                } else {
                    //result++;
                    result += pieceVal+r+1;
                }

            }
        }
        return result;




    }
}
