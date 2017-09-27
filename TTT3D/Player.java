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
	
    private final int MAX_DEPTH = 1;
	
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
            mStates.add(new M(g,evaluationFunction(g,Constants.CELL_X)));
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
      //  nextStates = evalSort(nextStates);
        int v;
        
        if (depth==0 || nextStates.isEmpty()){
            v = evaluationFunction(gameState,Constants.CELL_X);
            s.add(v);
            //return nextStates.elementAt(v);
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
           // return nextStates.elementAt(alpha);
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
            //return nextStates.elementAt(beta);

        }

     // 
        return nextStates.elementAt(s.indexOf(Collections.max(s)));

    }


    public int alphabeta(GameState gameState, int depth, int alpha, int beta, int player){
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        /*
        if (depth>0) {
        	nextStates = evalSort(nextStates);
        }
        */
        int v;

        if (depth==0 || nextStates.isEmpty()){
            v = evaluationFunction(gameState,Constants.CELL_X);
            return v;
        } else if (player==Constants.CELL_X){
            v = Integer.MIN_VALUE;
            for (GameState g: nextStates){
                v = Math.max(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_O));
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                	return alpha;
                   // break;
                }
            }
            return alpha;
        } else {
            v = Integer.MAX_VALUE;
            for (GameState g: nextStates){
                v = Math.min(v, alphabeta(g,depth-1,alpha,beta,Constants.CELL_X));
                beta = Math.min(beta,v);
                if (beta <= alpha){
                	return beta;
                    //break;
                }
            }
            return beta;
        }
  //      return v;

    }

   public int evaluationFunction(GameState state, int player){
	   
       int thisStatesScore = 0;
       /*
       if(state.isXWin()) {
    	   return 1000000;
       }
       
       if(state.isOWin()) {
    	   return 0;
       }
       */
   //    if(state.isXWin()) {
   // 	   thisStatesScore += 100000000; 
     //  }
      
       
       
       //for(int num = 0; num<GameState.CELL_COUNT; num++) {
    //	   
     //  }
       
       // Layer = z
       // row = y
       // col = x
       /*
       
       // all layers out the z-axis 
       for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
           // Sum all points for rows in each layer
           for(int row = 0; row<GameState.BOARD_SIZE; row++){
               thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, layer, layer, player);
           }
           for(int col = 0; col<GameState.BOARD_SIZE; col++){
	           thisStatesScore += myMarks(state, 0, GameState.BOARD_SIZE-1, col, col, layer, layer, player);
	       }
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,layer,layer,player);
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,layer,layer,player);
           //thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,0,layer,layer,player);
           //thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1,0,0,GameState.BOARD_SIZE-1,layer,layer,player);
       }
       
       // up from the y-axis
       for(int row = 0; row<GameState.BOARD_SIZE; row ++) {
    	   // sum all points for each layer
    	   for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
               thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, layer, layer, player);
    	   }
    	   for(int col = 0; col<GameState.BOARD_SIZE; col++) {
    		   thisStatesScore += myMarks(state, row, row, col, col, 0, GameState.BOARD_SIZE-1, player);
    	   }
           thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, 0, GameState.BOARD_SIZE-1,player);
           thisStatesScore += myMarks(state, row, row, 0, GameState.BOARD_SIZE-1, GameState.BOARD_SIZE-1, 0,player);
         //  thisStatesScore += myMarks(state, row, row, GameState.BOARD_SIZE-1, 0, 0, GameState.BOARD_SIZE-1,player);
          // thisStatesScore += myMarks(state, row, row, GameState.BOARD_SIZE-1, 0, GameState.BOARD_SIZE-1, 0,player);
       }
       
       // up from the x-axis
       for(int col = 0; col<GameState.BOARD_SIZE; col ++) {
    	   // all rows
    	   for(int row = 0; row<GameState.BOARD_SIZE; row++) {
               thisStatesScore += myMarks(state, row, row, col, col, 0, GameState.BOARD_SIZE-1, player);
    	   }
    	   // layers
    	   for(int layer = 0; layer<GameState.BOARD_SIZE; layer++) {
    		   thisStatesScore += myMarks(state, 0, GameState.BOARD_SIZE-1, col, col, layer, layer, player);
    	   }
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,col,col,0,GameState.BOARD_SIZE-1,player);
           thisStatesScore += myMarks(state, 0,GameState.BOARD_SIZE-1,col,col,GameState.BOARD_SIZE-1,0,player);
         //  thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1, 0, col,col,0,GameState.BOARD_SIZE-1,player);
          // thisStatesScore += myMarks(state, GameState.BOARD_SIZE-1, 0,col,col,GameState.BOARD_SIZE-1,0,player);
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
       
       
       
       
       
       */
       
       		for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
    	      for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
    	    	  thisStatesScore += myMarks(state,row,row,col,col,0,GameState.BOARD_SIZE-1,player);
    	      }
       		}
    	    for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
    	      for (int layer = 0; layer < GameState.BOARD_SIZE; ++layer) {
    	    	thisStatesScore += myMarks(state,row,row,0,GameState.BOARD_SIZE-1,layer,layer,player); 
    	      }
    	    }
    	    for (int col = 0; col < GameState.BOARD_SIZE; ++col)
    	      for (int layer = 0; layer < GameState.BOARD_SIZE; ++layer) {
    	    	thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,col,col,layer,layer,player);
    	      }

    	    for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
    	    	thisStatesScore += myMarks(state,row,row,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,player);
    	    }
    	    for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
    	      thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,col,col,0,GameState.BOARD_SIZE-1,player);
    	    }
    	    for (int layer = 0; layer < GameState.BOARD_SIZE; ++layer) {
    	      thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,layer,layer,player);
    	    }

    	    for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
    	      thisStatesScore += myMarks(state,row,row,0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,player);
    	    }
    	    for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
    	      thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,col,col,GameState.BOARD_SIZE-1,0,player);	
    	    }
    	    for (int layer = 0; layer < GameState.BOARD_SIZE; ++layer) {
    	      thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,layer,layer,player);	
    	    }

    	    thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,player);
    	    thisStatesScore += myMarks(state,0,GameState.BOARD_SIZE-1,GameState.BOARD_SIZE-1,0,0,GameState.BOARD_SIZE-1,player);
    	    thisStatesScore += myMarks(state,GameState.BOARD_SIZE-1,0,0,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,player);
    	    thisStatesScore += myMarks(state,GameState.BOARD_SIZE-1,0,GameState.BOARD_SIZE-1,0,0,GameState.BOARD_SIZE-1,player);
              
       return thisStatesScore;
   }
   
   public int myMarks(GameState state, int row, int row2, int col, int col2, int layer, int layer2, int player) {
       int XinARow = 0;
       int OinARow = 0;
       int maxX = 0;
       int linkedCounter = 0;
       int maxO = 0;
       int emptySpaces = 0;
       int dRow = (row2 - row) / (GameState.BOARD_SIZE - 1);
       int dCol = (col2 - col) / (GameState.BOARD_SIZE - 1);
       int dLayer = (layer2 - layer) / (GameState.BOARD_SIZE -1);
       int sum = 0;
       int oldPiece = 0;
       for (int i = 0; i < GameState.BOARD_SIZE; i++) {
           int piece = state.at(row + dRow*i, col + dCol * i, layer + dLayer * i);
           if (piece == Constants.CELL_X) {
        	   XinARow++;
           }
           if(piece == Constants.CELL_O) {
        	   OinARow ++;
           }
        	 //  sum += 1;
         
        //   if( oldPiece == Constants.CELL_O && piece == Constants.CELL_O) {
        //	   return 0;
         //  }
          // oldPiece = piece;
       }
       
       int power = XinARow - OinARow;
       
       return power;
       
  //     if(power<0) {
   // 	   return -1*(int)Math.pow(10,Math.abs(power));
     //  }   
      // return (int)Math.pow(10,power);
       
       /*
       if(XinARow>0) {
    	   sum += 10;
       }
       if(XinARow>1) {
    	   sum += 100;
       }
       if(XinARow>2) {
    	   sum += 1000;
       }
       if(XinARow>3) {
    	   sum += 10000;
       }
      
       if(linkedCounter>0) {
    	   sum += 500;
       }
       if(linkedCounter>1) {
    	   sum += 5000;
       }
       if(linkedCounter>2) {
    	   sum += 10000;
       }
       if(linkedCounter>3) {
    	   sum += 100000;
       }
       */
      // return sum;
       
   }
    
}
