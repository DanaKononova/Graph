import java.util.*;

import static java.util.Arrays.fill;

public class Graphs {
    private static int[][] nodeArray;
    private static final Scanner in = new Scanner(System.in);
    private static final ArrayList<String> ways = new ArrayList<>();
    private static int maxLen;
    private static String maxWayRoot;

    public static void main(String[] args) {
        System.out.print("Введите количество вершин графа: ");
        int nodeArraySize = in.nextInt();
        nodeArray = new int[nodeArraySize][nodeArraySize];
        inputGraph();
        outputGraph();
        mainMenu();
    }

    public static void mainMenu() {
        while (true) {
            System.out.println();
            System.out.println("Введите 1, чтобы найти вершину-источник.");
            System.out.println("Введите 2, чтобы найти результирующую вершину.");
            System.out.println("Введите 3, чтобы найти кратчайший путь между вершинами.");
            System.out.println("Введите 4, чтобы найти самый длинный путь между вершинами.");
            System.out.println("Введите 5, чтобы найти все пути между вершинами по возрастанию.");
            System.out.println("Введите 6, чтобы найти центр графа.");
            System.out.println("Введите 7, чтобы закончить программу.");
            int operation = in.nextInt();
            switch (operation) {
                case 1: {
                    int sourceNode = sourceNode();
                    if (sourceNode != 0) System.out.println("Вершина источник = " + sourceNode);
                    else System.out.println("Нет вершины источника.");
                    break;
                }
                case 2: {
                    int stockNode = stockNode();
                    if (stockNode != 0) System.out.println("Результирующая вершина = " + stockNode);
                    else System.out.println("Нет результирующей вершины.");
                    break;
                }
                case 3:
                case 4:
                case 5: {
                    System.out.print("Введите начальную вершину: ");
                    int start = in.nextInt();
                    System.out.print("Введите конечную вершину: ");
                    int end = in.nextInt();
                    if (operation == 3) findClosestWay(start - 1, end - 1);
                    else if (operation == 4) findLongestWay(start - 1, end - 1);
                    else findAllWays(start - 1, end - 1);
                    break;
                }
                case 6: {
                    System.out.println("Центр графа = " + findCenter());
                    break;
                }
                case 7:
                    return;
            }
        }
    }

    public static void inputGraph() {
        System.out.println("Введите матрицу смежности графа:");
        for (int i = 0; i < nodeArray.length; i++) {
            for (int j = 0; j < nodeArray.length; j++) {
                nodeArray[i][j] = in.nextInt();
            }
        }
    }

    public static void outputGraph() {
        for (int[] tableString : nodeArray) {
            for (int node : tableString) {
                System.out.print(node + " ");
            }
            System.out.println();
        }
    }

    public static int sourceNode() {
        boolean findSource;
        for (int i = 0; i < nodeArray.length; i++) {
            findSource = true;
            for (int j = 0; j < nodeArray.length; j++) {
                if (nodeArray[j][i] != 0) {
                    findSource = false;
                    break;
                }
            }
            if (findSource) return i + 1;
        }
        return 0;
    }

    public static int stockNode() {
        boolean findStock;
        for (int i = 0; i < nodeArray.length; i++) {
            findStock = true;
            for (int j = 0; j < nodeArray.length; j++) {
                if (nodeArray[i][j] != 0) findStock = false;
            }
            if (findStock) return i + 1;
        }
        return 0;
    }

    public static void findClosestWay(int start, int end) {
        int INF = Integer.MAX_VALUE / 2; // "Бесконечность"
        int nodeKol = nodeArray.length;
        /* Алгоритм Дейкстры за O(V^2) */
        boolean[] used = new boolean[nodeKol]; // массив пометок
        int[] dist = new int[nodeKol]; // массив расстояния. dist[v] = минимальное_расстояние(start, v)

        fill(dist, INF); // устанавливаем расстояние до всех вершин INF
        dist[start] = 0; // для начальной вершины положим 0

        while (true) {
            int closestNode = -1;
            for (int nv = 0; nv < nodeKol; nv++) {    // перебираем вершины
                if (!used[nv] && dist[nv] < INF && (closestNode == -1 || dist[closestNode] > dist[nv]))
                    closestNode = nv;    // выбираем самую близкую непомеченную вершину
            }
            if (closestNode == -1) break; // ближайшая вершина не найдена
            used[closestNode] = true; // помечаем ее
            for (int nv = 0; nv < nodeKol; nv++) {
                if (!used[nv] && nodeArray[closestNode][nv] < INF && nodeArray[closestNode][nv] != 0) // для всех непомеченных смежных
                    dist[nv] = Math.min(dist[nv], dist[closestNode] + nodeArray[closestNode][nv]); // улучшаем оценку расстояния
            }
        }
        int destination = dist[end];
        StringBuilder root = new StringBuilder().append(end + 1).append(">-");
        while (end != start) {
            for (int j = 0; j < nodeKol; j++) {
                if (nodeArray[j][end] != 0) {
                    if (dist[end] == nodeArray[j][end] + dist[j]) {
                        end = j;
                        root.append(j + 1).append(">-");
                        break;
                    }
                }
            }
        }
        root.reverse();
        root.delete(0, 2);
        System.out.println("Самый короткий путь: " + root + ", длина = " + destination);
    }

    public static void findLongestWay(int start, int end) {
        maxLen = 0;
        maxWayRoot = "";
        StringBuilder way = new StringBuilder().append(start + 1);
        find(start, end, way, 0);
        StringBuilder result = new StringBuilder(maxWayRoot);
        for (int i = 1; i < result.length(); i += 3) {
            result.insert(i, "->");
        }
        System.out.println("Самый длинный путь: " + result + ", длина = " + maxLen);
    }

    public static void find(int start, int end, StringBuilder way, int wayLen) {
        if (start == end) {
            if (wayLen > maxLen) {
                maxLen = wayLen;
                maxWayRoot = way.toString();
            }
        } else {
            for (int i = 0; i < nodeArray.length; i++) {
                if ((nodeArray[start][i] != 0)) {
                    if ((way.length() < 2) || ((i + 1) != Integer.parseInt(String.valueOf(way.charAt(way.length() - 2))))) {
                        wayLen += nodeArray[start][i];
                        way.append(i + 1);
                        find(i, end, way, wayLen);
                        wayLen -= nodeArray[start][i];
                        if (i + 1 >= 10) way.delete(way.length() - 2, way.length());
                        else way.deleteCharAt(way.length() - 1);
                    }
                }
            }
        }
    }

    public static void findAllWays(int start, int end) {
        ways.clear();
        StringBuilder way = new StringBuilder().append(start + 1);
        find(start, end, way);
        sortWays();
    }

    public static void find(int start, int end, StringBuilder way) {
        if (start == end) {
            ways.add(way.toString());
        } else {
            for (int i = 0; i < nodeArray.length; i++) {
                if ((nodeArray[start][i] != 0)) {
                    if ((way.length() < 2) || ((i + 1) != Integer.parseInt(String.valueOf(way.charAt(way.length() - 2))))) {
                        way.append(i + 1);
                        find(i, end, way);
                        if (i + 1 >= 10) way.delete(way.length() - 2, way.length());
                        else way.deleteCharAt(way.length() - 1);
                    }
                }
            }
        }
    }

    public static void sortWays() {
        HashMap<String, Integer> waysMap = new HashMap<>();
        for (String way : ways) {
            int length = 0;
            for (int j = 0; j < way.length() - 1; j++) {
                int firInd = Integer.parseInt(way.substring(j, j + 1)) - 1;
                int secInd = Integer.parseInt(way.substring(j + 1, j + 2)) - 1;
                length += nodeArray[firInd][secInd];
            }
            StringBuilder newWay = new StringBuilder();
            for (int i = 0; i < way.length(); i++) {
                newWay.append(way.charAt(i) + "->");
            }
            newWay.delete(newWay.length() - 2, newWay.length());
            waysMap.put(newWay.toString(), length);
        }
        System.out.println("Все пути и их длины:");
        waysMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .forEach(System.out::println);
    }

    private static int[][] floid() {
        int vNum = nodeArray.length;
        int[][] dist = new int[vNum][vNum]; // dist[i][j] = минимальное_расстояние(i, j)
        for (int i = 0; i < vNum; i++) System.arraycopy(nodeArray[i], 0, dist[i], 0, vNum);
        for (int k = 0; k < vNum; k++)
            for (int i = 0; i < vNum; i++)
                for (int j = 0; j < vNum; j++){
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    System.out.println(Arrays.deepToString(dist));
                }


        return dist;
    }

    public static int findCenter() {
        int[][] closestWays = floid();
        int max = closestWays[0][0];
        for (int i = 1; i < closestWays.length; i++) {
            if (closestWays[i][0] > max) max = closestWays[i][0];
        }

        int minInd = 0;
        int minValue = max;
        for (int j = 1; j < closestWays.length; j++) {
            max = closestWays[0][j];
            for (int i = 1; i < closestWays.length; i++) {
                if (closestWays[i][j] > max) max = closestWays[i][j];
            }
            if (max < minValue) {
                minValue = max;
                minInd = j;
            }
        }
        return minInd + 1;
    }


//0 10 30 50 10 0 0 0 0 0 0 0 0 0 10 0 40 20 0 0 10 0 10 30 0
// 0 7 9 0 0 14 0 0 10 15 0 0 0 0 0 11 0 2 0 0 0 0 6 0 0 0 0 6 0 0 0 0 0 0 9 0
}

