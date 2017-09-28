import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;


public class Player {
    /**
     *
     *
     * GLOBALS
     *
     *
     */
    private boolean isBetter = true;
    private boolean hasSeenBottom = false;
    private int MAXER = Constants.CELL_WHITE;
    private int MINER = Constants.CELL_RED;
    private final long TIME_LIMIT = 50000000;
    private final int MAX_DEPTH = 15;
    private final int MAN_VALUE = 5;
    private final int KING_VALUE = 50;
    private final int BOARD_SIZE = 8; // 8 COLS X 8 ROWS
    private static int[][] zobrist = init();
    private static final int CHECKERS_STATES = 32;
    private static final int DIFFERENT_PLAYER_TYPES = 4; // 0 white pawn, 1 red pawn, 2 white king, 3 red king
    private Hashtable<Integer, HashInfo> STATE_INFO = new Hashtable<>();


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
        MAXER = gameState.getNextPlayer();
        MINER = nextStates.firstElement().getNextPlayer();
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
        STATE_INFO = new Hashtable<>();

        //return nextStates.elementAt(s.indexOf(Collections.max(s)));


        //return alphabeta(gameState);
        isBetter = true;

        GameState oldBest = new GameState();
        GameState best = new GameState();
        for (int d = 0; d<MAX_DEPTH && deadline.timeUntil()>50000000 ;d++){
            hasSeenBottom = false;
            best = alphabeta(gameState,d,deadline);
            if (isBetter) oldBest = best;
            if(STATE_INFO.get(zhash(best)) != null) System.err.println(STATE_INFO.get(zhash(best)).getAlpha()+"   "+STATE_INFO.get(zhash(best)).getBeta());
            if(STATE_INFO.get(zhash(best)) != null && STATE_INFO.get(zhash(best)).getAlpha()==100000) break;
        }

        return oldBest;

        //return alphabeta(gameState,MAX_DEPTH);


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

    public class HashInfo{

        public GameState bestGameState;
        public int evaluation;
        public int alpha;
        public int beta;
        public int depth;

        public HashInfo() {
            this.alpha = Integer.MIN_VALUE;
            this.beta = Integer.MAX_VALUE;
        }

        public HashInfo(GameState state, int evaluation, int alpha, int beta, int depth) {
            this.bestGameState = state;
            this.evaluation = evaluation;
            this.alpha = alpha;
            this.beta = beta;
            this.depth = depth;
        }

        public void setBestGameState(GameState state) {
            this.bestGameState = state;
        }

        public void setEvalaution(int evaluation) {
            this.evaluation = evaluation;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        public void setBeta(int beta) {
            this.beta = beta;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public GameState getBestState() {
            return bestGameState;
        }
        public Integer getEvaluation() {
            return evaluation;
        }
        public Integer getAlpha() {
            return alpha;
        }
        public Integer getBeta() {
            return beta;
        }
        public Integer getDepth() {
            return depth;
        }


        public String print() {
            return "alpha: "+this.alpha+" beta: "+this.beta;
        }
    }

    public Vector<GameState> evalSort(Vector<GameState> states){
        Vector<M> mStates = new Vector<>();
        if (states.isEmpty()){return states;}
        for (GameState g:states){
            mStates.add(new M(g,evaluationFunction(g)));
        }
        int player = states.firstElement().getNextPlayer();
        if (player == MINER) mStates.sort(Comparator.comparing(M::getEval).reversed());
        else mStates.sort(Comparator.comparing(M::getEval));

        return new Vector<GameState>(mStates.stream().map(M::getGameState).collect(Collectors.toList()));
        //return states;

    }



    public GameState alphabeta(GameState gameState, int d, Deadline due){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depth = d;
        int player = gameState.getNextPlayer();
        int z = zhash(gameState);
        int bestStateV = 0;
        HashInfo currStateInfo = STATE_INFO.get(z);
        GameState bestNextState = new GameState();
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        nextStates = evalSort(nextStates);
        if(!nextStates.isEmpty()) bestNextState = nextStates.firstElement();
        if (currStateInfo != null && currStateInfo.getDepth()>= depth) {
            alpha = Math.max(alpha,currStateInfo.getAlpha());
            beta = Math.min(beta,currStateInfo.getBeta());
            bestNextState = currStateInfo.getBestState();
        }
        int oldAlpha = alpha;
        int oldBeta = beta;
        Vector<Integer> s = new Vector<>();
        int v;

        if (depth==0 || nextStates.isEmpty()){
            hasSeenBottom = true;
            v = evaluationFunction(gameState);
            s.add(v);
        } else if (player==MAXER){
            v = Integer.MIN_VALUE;
            for (int i = -1;i<nextStates.size();i++){
                if (due.timeUntil()<TIME_LIMIT) {isBetter=false; break;}
                if (i==-1) {
                    v = Math.max(v, alphabeta(bestNextState, depth - 1, alpha, beta, MINER, due));
                    bestStateV = v;
                } else {
                    v = Math.max(v, alphabeta(nextStates.elementAt(i),depth-1,alpha,beta,MINER,due));
                    s.add(v);
                }
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (int i = -1;i<nextStates.size();i++){
                if (due.timeUntil()<TIME_LIMIT) {isBetter=false; break;}
                if (i==-1){
                    v = Math.min(v, alphabeta(bestNextState,depth-1,alpha,beta,MAXER,due));
                    bestStateV = v;
                }
                else {
                    v = Math.min(v, alphabeta(nextStates.elementAt(i),depth-1,alpha,beta,MAXER,due));
                    s.add(v);
                }
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }
            }

        }

        if( depth>0 && !nextStates.isEmpty()){
            HashInfo newHashInfo = new HashInfo();
            if (nextStates.isEmpty()) {
                newHashInfo.setBestGameState(null);
            } else if(depth == 1){
                newHashInfo.setBestGameState(nextStates.firstElement());
            } else {
                if (s.isEmpty()) newHashInfo.setBestGameState(bestNextState);
                else if (player == MAXER) {
                    if (bestStateV>=Collections.max(s))
                        newHashInfo.setBestGameState(bestNextState);
                    else
                        newHashInfo.setBestGameState(nextStates.elementAt(s.indexOf(Collections.max(s))));
                } else {
                    if (bestStateV<=Collections.min(s))
                        newHashInfo.setBestGameState(bestNextState);
                    else
                        newHashInfo.setBestGameState(nextStates.elementAt(s.indexOf(Collections.min(s))));
                }
            }
            if (v<=oldAlpha) newHashInfo.setBeta(v);
            else if (v > oldAlpha && v < oldBeta) {
                newHashInfo.setAlpha(v);
                newHashInfo.setBeta(v);
            } if (v >= oldBeta) {
                newHashInfo.setAlpha(v);
            }
            newHashInfo.setDepth(depth);
            newHashInfo.setEvalaution(v);
            STATE_INFO.remove(z);
            STATE_INFO.put(z,newHashInfo);
        }

        return s.isEmpty() ? bestNextState : nextStates.elementAt(s.indexOf(Collections.max(s)));


    }


    public int alphabeta(GameState gameState, int depth, int alpha, int beta, int player, Deadline due){
        int oldAlpha = alpha;
        int oldBeta = beta;
        int z = zhash(gameState);
        int bestStateV = 0;
        Vector<Integer> s = new Vector<>();
        HashInfo currStateInfo = STATE_INFO.get(z);
        GameState bestNextState = new GameState();
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        nextStates = evalSort(nextStates);
        if(!nextStates.isEmpty()) bestNextState = nextStates.firstElement();
        if (currStateInfo != null && currStateInfo.getDepth()>= depth) {
            //System.err.println(currStateInfo.getAlpha() + " "+ beta);
            //System.err.println(currStateInfo.getBeta() + " "+ alpha);
            if (currStateInfo.getAlpha()>=beta && hasSeenBottom) return currStateInfo.getAlpha();
            if (currStateInfo.getBeta()<=alpha && hasSeenBottom) return currStateInfo.getBeta();
            alpha = Math.max(alpha,currStateInfo.getAlpha());
            beta = Math.min(beta,currStateInfo.getBeta());
            bestNextState = currStateInfo.bestGameState;
        }
        int v;

        if (depth==0 || nextStates.isEmpty()){
            hasSeenBottom = true;
            v = evaluationFunction(gameState);
            s.add(v);
        } else if (player==MAXER){
            v = Integer.MIN_VALUE;
            for (int i = -1;i<nextStates.size();i++){
                if (due.timeUntil()<TIME_LIMIT) {isBetter=false; break;}
                if (i==-1) {
                    v = Math.max(v, alphabeta(bestNextState, depth - 1, alpha, beta, MINER, due));
                    bestStateV = v;
                } else {
                    v = Math.max(v, alphabeta(nextStates.elementAt(i),depth-1,alpha,beta,MINER,due));
                    s.add(v);
                }
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE;
            for (int i = -1;i<nextStates.size();i++){
                if (due.timeUntil()<TIME_LIMIT) {isBetter=false; break;}
                if (i==-1){
                    v = Math.min(v, alphabeta(bestNextState,depth-1,alpha,beta,MAXER,due));
                    bestStateV = v;
                }
                else {
                    v = Math.min(v, alphabeta(nextStates.elementAt(i),depth-1,alpha,beta,MAXER,due));
                    s.add(v);
                }
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }
            }

        }

        if (depth > 0 && !nextStates.isEmpty()){
            HashInfo newHashInfo = new HashInfo();
            if (nextStates.isEmpty()) {
                newHashInfo.setBestGameState(null);
            } else if (depth == 1){
                newHashInfo.setBestGameState(nextStates.firstElement());
            } else {
                if (s.isEmpty()) newHashInfo.setBestGameState(bestNextState);
                else if (player == MAXER) {
                    if (bestStateV>=Collections.max(s))
                        newHashInfo.setBestGameState(bestNextState);
                    else
                        newHashInfo.setBestGameState(nextStates.elementAt(s.indexOf(Collections.max(s))));
                } else {
                    if (bestStateV<=Collections.min(s))
                        newHashInfo.setBestGameState(bestNextState);
                    else
                        newHashInfo.setBestGameState(nextStates.elementAt(s.indexOf(Collections.min(s))));
                }
            }
            if (v<=oldAlpha) newHashInfo.setBeta(v);
            else if (v > oldAlpha && v < oldBeta) {
                newHashInfo.setAlpha(v);
                newHashInfo.setBeta(v);
            } else if (v >= oldBeta) {
                newHashInfo.setAlpha(v);
            }
            newHashInfo.setDepth(depth);
            newHashInfo.setEvalaution(v);

            STATE_INFO.remove(z);
            STATE_INFO.put(z,newHashInfo);
        }
        return v;

    }

    public int evaluationFunction(GameState gameState){

        int result = 0;
        int numberOfPieces = 0;
        if (MAXER == Constants.CELL_WHITE && gameState.isWhiteWin()){
            return 100000;
        } else if (MAXER == Constants.CELL_RED && gameState.isRedWin()) {
            return 100000;
        } else if (gameState.isEOG() && !gameState.isRedWin() && !gameState.isWhiteWin()) {
            return 0;
        } else if (MINER == Constants.CELL_WHITE && gameState.isWhiteWin()){
            return -100000;
        } else if (MINER == Constants.CELL_RED && gameState.isRedWin()) {
            return -100000;
        }

        for (int r = 0; r < BOARD_SIZE; r++){
            for (int c = 0; c < BOARD_SIZE/2; c++){
                int cOffset = (r+1)%2;
                int piece = gameState.get(r,cOffset+2*c);
                int pieceVal = (piece & Constants.CELL_KING) != 0 ? KING_VALUE: MAN_VALUE;
                numberOfPieces++;
                if ((piece & MINER) != 0){
                    //result--;
                    if (pieceVal==KING_VALUE){
                        result -= pieceVal+checkIfProtected(gameState,r,cOffset+2*c,MINER);
                    } else {
                        result -= pieceVal + (BOARD_SIZE - r - 1) + checkIfProtected(gameState, r, cOffset + 2 * c, MINER);
                    }
                } else {
                    //result++;
                    if (pieceVal == KING_VALUE){
                        result += pieceVal+checkIfProtected(gameState,r,cOffset+2*c,MAXER);
                    } else {
                        result += pieceVal + r + 1 + checkIfProtected(gameState, r, cOffset + 2 * c, MAXER);
                    }
                }

            }
        }
        return result;

    }

    public int manhattan(int r1, int c1, int r2, int c2){
        return Math.abs(r1-r2)+Math.abs(c1-c2);
    }

    /*public int evaluationFunction(GameState gameState){
        int result = 0;
        if (MAXER == Constants.CELL_WHITE && gameState.isWhiteWin()){
            return 100000;
        } else if (MAXER == Constants.CELL_RED && gameState.isRedWin()) {
            return 100000;
        } else if (gameState.isEOG() && !gameState.isRedWin() && !gameState.isWhiteWin()) {
            return 0;
        } else if (MINER == Constants.CELL_WHITE && gameState.isWhiteWin()){
            return -100000;
        } else if (MINER == Constants.CELL_RED && gameState.isRedWin()) {
            return -100000;
        }
        int maxKings = 0;
        int minKings = 0;
        for (int r = 0; r < BOARD_SIZE; r++){
            for (int c = 0; c < BOARD_SIZE/2; c++){
                int cOffset = (r+1)%2;
                int piece = gameState.get(r,cOffset+2*c);
                if ((piece & Constants.CELL_KING) != 0){
                    if ((piece & MAXER) != 0) maxKings++;
                    else minKings++;
                }
                if (piece == Constants.CELL_INVALID || piece == Constants.CELL_EMPTY) continue;
                for (int i = 0; i<CHECKERS_STATES;i++){
                    int compPiece = gameState.get(i);
                    if ((piece & compPiece) == 0){
                        result += manhattan(r,c,GameState.cellToRow(i),GameState.cellToCol(i));
                    }
                }

            }
        }
        if (maxKings>minKings)


        return result;

    }*/




    public int checkIfProtected(GameState state, int r, int c, int player){
        int result = 0;
        if (r == 0 || r == 7 || c == 0 || c == 7){
            return 2;
        } else if (player == MAXER){
            int left = state.get(r-1,c-1);
            int right = state.get(r-1,c+1);
            if ((left & MAXER) != 0){ result++;}
            if ((right & MAXER) != 0){ result++;}
        } else if (player == MINER){
            int left = state.get(r+1,c-1);
            int right = state.get(r+1,c+1);
            if ((left & MINER) != 0){ result++;}
            if ((right & MINER) != 0){ result++;}
        }
        return result;
    }



    public static int[][] init() {
        int[][] newzobrist = new int[CHECKERS_STATES][DIFFERENT_PLAYER_TYPES];
        for(int i = 0; i<CHECKERS_STATES;i++) {
            for(int j = 0; j<DIFFERENT_PLAYER_TYPES;j++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                newzobrist[i][j] = randomNum;
            }
        }
        return newzobrist;
    }

    public int zhash(GameState state) {
        int val = 0;
        boolean king = false;
        for(int i = 0; i<CHECKERS_STATES; i++) {
            int p = 0;
            int piece = state.get(i);
            if(piece!=Constants.CELL_EMPTY && piece!=Constants.CELL_INVALID) {
                if((piece & Constants.CELL_KING) == 4) king = true;
                if(piece==Constants.CELL_WHITE) p = 0;
                if(piece==Constants.CELL_RED) p = 1;
                if(piece == 0 && king == true) p = 2;
                if(piece == 1 && king == true) p = 3;
                val ^= zobrist[i][p];
            }
        }
        return val;
    }

}
