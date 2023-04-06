import java.io.*;
import java.util.*;

public class ExpenseSplitter {
    /**
     * Имена массивом из заголовка инпута.
     */
    private String[] names;
    /**
     * Индекс по имени для вывода результатов.
     */
    private final Map<String, Integer> nameIndex;
    /**
     * Мапа долгов. Если сумма у имени > 0, то должны ему.
     */
    private final Map<String, Double> nameDebt;
    /**
     * Сет с упорядочением на основе nameDebt.
     */
    private final TreeSet<PersonFinance> personFinances;
    /**
     * Отчет о долгах.
     */
    private final List<DebtPayed> debtsList;
    /**
     * Двумерный массив долгов для вывода результатов.
     */
    private String[][] debitorMatrix;
    private static final String OUTPUT_FILE = "output_check.csv";

    public ExpenseSplitter() {
        nameDebt = new HashMap<>();
        personFinances = new TreeSet<>();
        debtsList = new ArrayList<>();
        nameIndex = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        ExpenseSplitter es = new ExpenseSplitter();
        es.readInput("input_check.csv");
        es.findEndebted();
        es.writeDebts();
    }

    public List<DebtPayed> getDebtsList() {
        return debtsList;
    }
    private void readInput(String inputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            setZeroDebts(br.readLine());
            while(br.ready()) {
                processLine(br.readLine());
            }
        }
        collectDebts();
    }

    /**
     * Долги на экран.
     */
    public void printDebts() {
        for (int j = 0; j < names.length - 2; j++) {
            System.out.print("," + names[j + 2].trim());
        }
        System.out.println("\n");
        for (int i = 0; i < names.length - 2; i++) {
            System.out.print(names[i + 2].trim());
            for (int j = 0; j < names.length - 2; j++) {
                System.out.print("," + debitorMatrix[i][j]);
            }
            System.out.println("\n");
        }
    }

    /**
     * Долги в файл.
     */
    public void writeDebts() throws IOException {
        try(FileWriter wf = new FileWriter(OUTPUT_FILE)) {
            for (int j = 0; j < names.length - 2; j++) {
                wf.write("," + names[j + 2].trim());
            }
            wf.write("\n");
            for (int i = 0; i < names.length - 2; i++) {
                wf.write(names[i + 2].trim());
                for (int j = 0; j < names.length - 2; j++) {
                    wf.write("," + debitorMatrix[i][j]);
                }
                wf.write("\n");
            }
        }
    }
    /**
     * Собрать сет по мапе.
     */
    private void collectDebts() {
        for (String name: nameDebt.keySet()) {
            personFinances.add(new PersonFinance(name, nameDebt.get(name)));
        }
    }

    /**
     * Инит:
     * - списка имен
     * - нулями долги
     * - мапы индекс-имя
     * - матрицы долгов
     */
    private void setZeroDebts(String firstLine) {
        names = firstLine.split(",");
        for (int i = 2; i < names.length; i++) {
            nameDebt.put(names[i].trim(), 0.0);
            nameIndex.put(names[i].trim(), i - 2);
        }
        debitorMatrix = new String[names.length - 2][names.length - 2];
        for (int i = 0; i < names.length - 2; i++) {
            for (int j = 0; j < names.length - 2; j++) {
                debitorMatrix[i][j] = "0";
            }
        }
    }

    /**
     * По каждой строке:
     * - в каждом столбце сумму по имени в столбце попадает в долг по этому имени
     * - сумма по строке это сколько имя заплатило;
     */
    private void processLine(String line) {
        String[] sums = line.split(",");
        double lineSum = 0;
        double sumInColumn;
        String nameInColumn;
        for (int i = 2; i < sums.length; i++) {
            nameInColumn = names[i].trim();
            if (!sums[i].isBlank()) {
                sumInColumn = Double.parseDouble(sums[i].trim());
                nameDebt.put(nameInColumn, nameDebt.get(nameInColumn) - sumInColumn);
                lineSum += sumInColumn;
            }
        }
        String nameInLine = sums[0].trim();
        nameDebt.put(nameInLine, nameDebt.get(nameInLine) + lineSum);
    }

    /**
     * Расчет кто кому должен с минимальным количество транзакций:
     * 0 - сет содержит суммы по возрастанию
     * 1 - транзакция: тот, кто больше всего должен платит тому, кто больше всех заплатил
     * 2 - если у имени остался долг, возвращаем в сет имя с этим остатком > 0
     * 3 - если имя осталось должно, возвращаем в сет имя с этим остатком < 0
     * 4 - при возврате в сет имя попадает в позицию согласно упорядочению, чтобы выполнить п. 1
     */
    private void findEndebted() {
        PersonFinance min;
        PersonFinance max;
        double debt;
        String twoDigits = "%.2f";
        while(personFinances.size() > 1) {
            min = personFinances.first();
            max = personFinances.last();
            debt = max.payedAmount + min.payedAmount;
            personFinances.remove(min);
            personFinances.remove(max);
            if (debt > 0) {
                personFinances.add(new PersonFinance(max.name, debt));
                debtsList.add(new DebtPayed(min.name, max.name, String.format(twoDigits, -min.payedAmount)));
                debitorMatrix[nameIndex.get(min.name)][nameIndex.get(max.name)]
                        = String.format(twoDigits, -min.payedAmount);
            } else if (debt < 0) {
                personFinances.add(new PersonFinance(min.name, debt));
                debtsList.add(new DebtPayed(min.name, max.name, String.format(twoDigits, max.payedAmount)));
                debitorMatrix[nameIndex.get(min.name)][nameIndex.get(max.name)]
                        = String.format(twoDigits, max.payedAmount);
            } else {
                debtsList.add(new DebtPayed(min.name, max.name, String.format(twoDigits, max.payedAmount)));
                debitorMatrix[nameIndex.get(min.name)][nameIndex.get(max.name)]
                        = String.format(twoDigits, max.payedAmount);
            }
        }
    }
}