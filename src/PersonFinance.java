import java.util.Objects;

class PersonFinance implements Comparable<PersonFinance>{
    String name;
    double payedAmount;

    /**
     * @param name              Имя
     * @param payedAmount       Сколько этому имени должны, если payedAmount > 0
     *                          Сколько это имя должно, если payedAmount < 0
     */

    public PersonFinance(String name, double payedAmount) {
        this.name = name;
        this.payedAmount = payedAmount;
    }
    @Override
    public int compareTo(PersonFinance o) {
        return Double.compare(this.payedAmount, o.payedAmount);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + ", " + payedAmount;
    }
}