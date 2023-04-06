class DebtPayed {
    String personPayed;
    String personAccepted;
    String amount;

    /**
     * Список результатов
     * @param personPayed       Кто платит долг
     * @param personAccepted    Кому платят долг
     * @param amount            Размер
     */

    public DebtPayed(String personPayed, String personAccepted, String amount) {
        this.personPayed = personPayed;
        this.personAccepted = personAccepted;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return personPayed + "->"
                + personAccepted + ":"
                + amount;
    }
}
