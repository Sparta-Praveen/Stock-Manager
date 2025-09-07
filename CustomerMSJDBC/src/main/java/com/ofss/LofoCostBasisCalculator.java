package com.ofss;
import java.util.*;

public class LofoCostBasisCalculator {
    static class Lot {
        int volume;
        double pricePerShare;
        Lot(int volume, double pricePerShare) {
            this.volume = volume;
            this.pricePerShare = pricePerShare;
        }
    }

    public static double calculateNetInvested(List<Transaction> transactions) {
        List<Lot> buyLots = new ArrayList<>();

        for (Transaction t : transactions) {
            if ("BUY".equalsIgnoreCase(t.getTransactionType())) {
                double pricePerShare = t.getTransactionPrice() / t.getVolume();
                buyLots.add(new Lot(t.getVolume(), pricePerShare));
            } else if ("SELL".equalsIgnoreCase(t.getTransactionType())) {
                int volumeToSell = t.getVolume();
                while (volumeToSell > 0 && !buyLots.isEmpty()) {
                    Lot lowestLot = Collections.min(
                        buyLots,
                        Comparator.comparingDouble(l -> l.pricePerShare)
                    );
                    if (lowestLot.volume > volumeToSell) {
                        lowestLot.volume -= volumeToSell;
                        volumeToSell = 0;
                    } else {
                        volumeToSell -= lowestLot.volume;
                        buyLots.remove(lowestLot);
                    }
                }
            }
        }
        double netInvested = 0;
        for (Lot lot : buyLots) {
            netInvested += lot.volume * lot.pricePerShare;
        }
        return netInvested;
    }

    public static int getCurrentVolume(List<Transaction> transactions) {
        int volume = 0;
        for (Transaction t : transactions) {
            if ("BUY".equalsIgnoreCase(t.getTransactionType())) {
                volume += t.getVolume();
            } else if ("SELL".equalsIgnoreCase(t.getTransactionType())) {
                volume -= t.getVolume();
            }
        }
        return volume;
    }
}