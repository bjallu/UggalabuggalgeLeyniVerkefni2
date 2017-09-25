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
	
    private final int MAX_DEPTH = 3;
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
        mStates.sort(Comparator.comparing(M::getEval).reversed());

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
       
       
       //for(int num = 0; num<GameState.CELL_COUNT; num++) {
    //	   
     //  }
       
       // Layer = z
       // row = y
       // col = x
       
       
       // all layers out the z-axis 
       for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
           // Sum all points for rows in each layer
           for(int row = 0; row<GameState.BOARD_SIZE; row++){
               thisStatesScore += myMarks(state, row,row,0,GameState.BOARD_SIZE,layer,layer,player);
           }
           for(int col = 0; col<GameState.BOARD_SIZE; col++){
	           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE,col,col,layer,layer,player);
	       }
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,layer,layer,player);
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,layer,layer,player);
       }
       
       // up from the y-axis
       for(int row = 0; row<GameState.BOARD_SIZE; row ++) {
    	   // sum all points for each layer
    	   for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
               thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE, layer, layer, player);
    	   }
    	   for(int col = 0; col<GameState.BOARD_SIZE; col++) {
    		   thisStatesScore += myMarks(state, row, row, col, col, 0, GameState.BOARD_SIZE, player);
    	   }
           thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, 0, GameState.BOARD_SIZE-1,player);
           thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, GameState.BOARD_SIZE-1, 0,player);
       }
       
       // up from the x-axis
       for(int col = 0; col<GameState.BOARD_SIZE; col ++) {
    	   // all rows
    	   for(int row = 0; row<GameState.BOARD_SIZE; row++) {
               thisStatesScore += myMarks(state, row, row, col, col, 0, GameState.BOARD_SIZE, player);
    	   }
    	   // layers
    	   for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
    		   thisStatesScore += myMarks(state, 0, GameState.BOARD_SIZE, col, col, layer, layer, player);
    	   }
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,col,col,0,GameState.BOARD_SIZE-1,player);
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,col,col,GameState.BOARD_SIZE-1,0,player);
       }
       
       // the four different diagonal lines that go from (0,0,0) to (max x, max y, max z)
       // , (0,max y, 0) to (max x, 0, max z), (max x, 0, 0) to (0, max y, max z)
       // and lastly from (max x, max y, 0) to (0, 0, max z)
       // 0,0,0
       thisStatesScore += myMarks(state, 0, GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,player);
       // max y
       thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1, 0, 0, GameState.BOARD_SIZE-1, 0,GameState.BOARD_SIZE-1,player);
       // max x
       thisStatesScore += myMarks(state, 0, GameState.BOARD_SIZE-1, GameState.BOARD_SIZE-1, 0, 0, GameState.BOARD_SIZE-1,player);
       // max x max y
       thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1, 0, GameState.BOARD_SIZE-1,0,0,GameState.BOARD_SIZE-1,player);
         
       return thisStatesScore;
   }
   
   public int myMarks(GameState state, int row, int row2, int col, int col2, int layer, int layer2, int player) {
       int XinARow = 0;
       int OinARow = 0;
       int maxX = 0;
       int maxO = 0;
       int emptySpaces = 0;
       int dRow = (row2 - row) / (GameState.BOARD_SIZE - 1);
       int dCol = (col2 - col) / (GameState.BOARD_SIZE - 1);
       int dLayer = (layer2 - layer) / (GameState.BOARD_SIZE -1);
       int oldPiece = state.at(row, col, layer);
       if (oldPiece == Constants.CELL_X) {
           XinARow++;
       } else if (oldPiece == Constants.CELL_O) {
           OinARow++;
       } else {
           emptySpaces++;
       }
       for (int i = 1; i < GameState.BOARD_SIZE; i++) {
           int piece = state.at(row + i * dRow, col + i * dCol, layer + i*dLayer);
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
