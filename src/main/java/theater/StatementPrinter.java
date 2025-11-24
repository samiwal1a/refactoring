package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 * @para invoice the invoice to print
 * @para plays the mapping of play IDs to plays
 *
 */

public class StatementPrinter {

    private static final double CENTS_TO_DOLLARS = 100.0;
    private Invoice invoice;
    private Map<String, Play> plays;

    public StatementPrinter(final Invoice invoice, final Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        final NumberFormat format1 = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance perf : invoice.getPerformances()) {

            final Play play = plays.get(perf.getPlayID());
            int thisAmount = 0;

            switch (play.getType()) {
                case "tragedy":
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    if (perf.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.TRAGEDY_EXTRA_AMOUNT_PER_PERSON
                                * (perf.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                    }
                    break;

                case "comedy":
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    if (perf.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (perf.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                    }
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * perf.getAudience();
                    break;

                default:
                    throw new RuntimeException("unknown type: " + play.getType());
            }
            volumeCredits += Math.max(
                    perf.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            if ("comedy".equals(play.getType())) {
                volumeCredits += perf.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    format1.format(thisAmount / CENTS_TO_DOLLARS),
                    perf.getAudience()
            ));

            totalAmount += thisAmount;
        }
        result.append(String.format(
                "Amount owed is %s%n",
                format1.format(totalAmount / CENTS_TO_DOLLARS)
        ));
        result.append(String.format(
                "You earned %s credits%n",
                volumeCredits
        ));

        return result.toString();
    }

    /**
     * Returns the invoice.
     *
     * @return the invoice
     */

    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    public void setPlays(final Map<String, Play> plays) {
        this.plays = plays;
    }
}
