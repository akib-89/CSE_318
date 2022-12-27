import java.util.List;

/**
 * This is the CSP solver class.
 * this has a Variable Heuristic.
 * this also has a Value Heuristic. (here this is constant)
 * the solve method takes a game board (CSP problem) and solves it.
 */
public class CSP_Solver {
    private final Variable_Heuristic variable_heuristic;



    /**
     * Constructor for the CSP solver class.
     * @param variable_heuristic    the variable heuristic.
     */
    public CSP_Solver(Variable_Heuristic variable_heuristic) {
        this.variable_heuristic = variable_heuristic;
    }

    /**
     * the preprocessor function:
     * This function takes the board then iterate through all over the board then shrinks the domain size of each variable
     * if the variable is not assigned in the first place
     * @param   gameBoard   the game board.
     *                      the CSP problem.
     * @return  the game board. with the domain size of each variable shrunk.
     */
    public GameBoard preprocessor(GameBoard gameBoard){
        Variable[][] board = gameBoard.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                process_index(i, j, gameBoard);
            }
        }
        return gameBoard;
    }

    private void process_index(int i, int j, GameBoard gameBoard){
        Variable[][] board = gameBoard.getBoard();


        for (int k = 0; k < board.length; k++) {
            if (board[i][k].getValue() != 0){
                board[i][j].removeValueFromDomain(board[i][k].getValue());
            }
            if (board[k][j].getValue() != 0){
                board[i][j].removeValueFromDomain(board[k][j].getValue());
            }
        }
    }



    // TODO: 12/26/22 implement the solve method.
    /**
     * This method solves the CSP problem.
     * @param gameBoard     the game board.
     * @return              the solved game board.
     */
    public GameBoard solve(GameBoard gameBoard) {
        return backTracking_search(gameBoard);
    }


    private GameBoard backTracking_search(GameBoard gameBoard){
        if (gameBoard.isComplete()){
            return gameBoard;
        }
        Variable variable = Variable_Order_Heuristic.getNextVariable(gameBoard.getVariables(), variable_heuristic);
        while (variable != null && getNextValue(variable, gameBoard.getVariables()) != 0) {
            variable.setValue(getNextValue(variable, gameBoard.getVariables()));
            if (Constraint.holds(gameBoard.getVariables())) {
                GameBoard solvedGameBoard = backTracking_search(gameBoard);
                if (solvedGameBoard != null) {
                    return solvedGameBoard;
                }
            }
            variable.setValue(0);
        }
        return null;
    }

    /**
     * This method returns the value heuristic.
     * here the least constraining value heuristic is used.
     * that is the value that rules out the least number of values in the remaining variables. is chosen.
     * @param variable          the variable whose value is to be chosen.
     * @param variables         the list of variables. (the CSP problem variables)
     * @return                  the value that rules out the least number of values in the remaining variables.
     *                          if no such value is found then return 0.
     */
    private int getNextValue(Variable variable, List<Variable> variables){
        int value = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < variable.getDomainSize(); i++) {
            int temp = 0;                                   // the number of values that will be ruled out.
            int val = variable.getValueFromDomain(i);
            for (Variable item : variables) {
                if (item.getValue() == 0) {
                    if (item.isInDomain(val)) {
                        temp++;
                    }
                }
            }
            if (temp < min){
                min = temp;
                value = variable.getValueFromDomain(i);
            }
        }
        return value;
    }



}
