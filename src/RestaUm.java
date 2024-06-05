import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaUm {
    // Constantes para o tamanho do tabuleiro e as direções dos movimentos
    private static final int SIZE = 7;
    private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    private static final int MAX_DEPTH = 31;  // Limite de profundidade para poda

    // Variáveis para representar o tabuleiro, movimentos feitos e estados visitados
    private final int[][] board;
    private final List<String> moves;
    private final Set<String> visitedStates;
    private int totalMoves;

    public static void main(String[] args) {
        RestaUm game = new RestaUm();
        game.printBoard();  // Imprime o tabuleiro inicial
        System.out.println("Aguarde, em processamento...");

        if (game.solve(0)) {  // Tenta resolver o jogo começando da profundidade 0
            System.out.println("Solução encontrada:");
            game.printBoard();  // Imprime o tabuleiro final
            System.out.println("Movimentos:");
            for (String move : game.moves) {
                System.out.println(move);  // Imprime todos os movimentos feitos
            }
            System.out.println("Total de movimentos: " + game.totalMoves);  // Imprime o total de movimentos
        } else {
            System.out.println("Nenhuma solução encontrada.");
        }
    }

    // Construtor da classe RestaUm, inicializa o tabuleiro e outras variáveis
    public RestaUm() {
        board = new int[SIZE][SIZE];
        moves = new ArrayList<>();
        visitedStates = new HashSet<>();
        totalMoves = 0;
        initializeBoard();
    }

    // Inicializa o tabuleiro com peças (1) e espaços vazios (-1 para fora do tabuleiro e 0 para o centro)
    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((i < 2 || i > 4) && (j < 2 || j > 4)) {
                    board[i][j] = -1;  // Espaços fora do tabuleiro são marcados com -1
                } else {
                    board[i][j] = 1;   // Peças são marcadas com 1
                }
            }
        }
        board[3][3] = 0;  // O centro do tabuleiro começa vazio
    }

    // Verifica se um movimento é válido
    private boolean isValidMove(int x, int y, int dx, int dy) {
        int nx = x + dx;  // Nova posição da peça
        int ny = y + dy;
        int mx = x + dx / 2;  // Posição intermediária da peça a ser "comida"
        int my = y + dy / 2;
        return (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == 0 && board[mx][my] == 1);
    }

    // Executa um movimento
    private boolean makeMove(int x, int y, int dx, int dy) {
        if (isValidMove(x, y, dx, dy)) {
            board[x][y] = 0;  // Remove a peça da posição original
            board[x + dx / 2][y + dy / 2] = 0;  // Remove a peça intermediária
            board[x + dx][y + dy] = 1;  // Coloca a peça na nova posição
            String move = "(" + (x + 1) + "," + (y + 1) + ") -> (" + (x + dx + 1) + "," + (y + dy + 1) + ")";
            moves.add(move);  // Adiciona o movimento à lista de movimentos
            totalMoves++;  // Incrementa o contador de movimentos
            return true;
        }
        return false;
    }

    // Desfaz um movimento
    private void undoMove(int x, int y, int dx, int dy) {
        board[x][y] = 1;  // Recoloca a peça na posição original
        board[x + dx / 2][y + dy / 2] = 1;  // Recoloca a peça intermediária
        board[x + dx][y + dy] = 0;  // Remove a peça da nova posição
        moves.removeLast();  // Remove o último movimento da lista
        totalMoves++;  // Incrementa o contador de movimentos
    }

    // Função recursiva para resolver o jogo
    private boolean solve(int depth) {
        if (depth > MAX_DEPTH) {
            return false;  // Se a profundidade exceder o máximo permitido, aborta
        }

        String currentState = boardToString();
        if (visitedStates.contains(currentState)) {
            return false;  // Se o estado atual já foi visitado, aborta
        }
        visitedStates.add(currentState);  // Marca o estado atual como visitado

        if (remainingPieces() == 1) {
            return true;  // Se restar apenas uma peça, a solução foi encontrada
        }

        List<int[]> moveOptions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    for (int[] dir : DIRECTIONS) {
                        if (isValidMove(i, j, dir[0], dir[1])) {
                            moveOptions.add(new int[]{i, j, dir[0], dir[1]});
                        }
                    }
                }
            }
        }

        // Ordena os movimentos priorizando aqueles que movem a peça mais para o centro
        moveOptions.sort(Comparator.comparingInt(a -> Math.abs(a[0] - 3) + Math.abs(a[1] - 3) - Math.abs((a[0] + a[2]) - 3) - Math.abs((a[1] + a[3]) - 3)));

        for (int[] move : moveOptions) {
            if (makeMove(move[0], move[1], move[2], move[3])) {
                if (solve(depth + 1)) {  // Chama recursivamente para tentar resolver a partir deste movimento
                    return true;
                }
                undoMove(move[0], move[1], move[2], move[3]);  // Desfaz o movimento se não levou à solução
            }
        }

        return false;  // Se nenhum movimento levar à solução, retorna falso
    }

    // Conta o número de peças restantes no tabuleiro
    private int remainingPieces() {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    // Converte o estado atual do tabuleiro para uma string para facilitar a verificação de estados visitados
    private String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    // Imprime o estado atual do tabuleiro
    private void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == -1) {
                    System.out.print("  ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
