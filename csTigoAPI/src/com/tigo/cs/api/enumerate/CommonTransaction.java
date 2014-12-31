package com.tigo.cs.api.enumerate;

/**
 *
 * @author Miguel Zorrilla
 */
public enum CommonTransaction {
    /***/
    /**Search a customer by code*/
    SEARCH_CLIENT_BYCODE(1,
    "commons.transaction.name.SearchClientByCode",
    "commons.transaction.message.SearchClientByCode",
    "commons.transaction.nomatchmessage.SearchClientByCode"),
    /**Search a customer by name*/
    SEARCH_CLIENT_BYNAME(2,
    "commons.transaction.name.SearchClientByName",
    "commons.transaction.message.SearchClientByName",
    "commons.transaction.nomatchmessage.SearchClientByName"),
    /**Search a product by code*/
    SEARCH_PRODUCT_BYCODE(3,
    "commons.transaction.name.SearchProductByCode",
    "commons.transaction.message.SearchProductByCode",
    "commons.transaction.nomatchmessage.SearchProductByCode"),
    /**Search a product by name*/
    SEARCH_PRODUCT_BYNAME(4,
    "commons.transaction.name.SearchProductByName",
    "commons.transaction.message.SearchProductByName",
    "commons.transaction.nomatchmessage.SearchProductByName"),
    ENTRY(5,
    "commons.transaction.name.Entry",
    "commons.transaction.message.Entry",
    ""),
    /**Search a product by name*/
    EXIT(6,
    "commons.transaction.name.Exit",
    "commons.transaction.message.Exit",
    "");
    protected final int value;
    protected final String description;
    protected final String succesMessage;
    protected final String noMatchMessage;

    private CommonTransaction(int value, String description, String succesMessage, String noMatchMessage) {
        this.value = value;
        this.description = description;
        this.succesMessage = succesMessage;
        this.noMatchMessage = noMatchMessage;
    }

    public int value() {
        return value;
    }

    public String description() {
        return description;
    }

    public String noMatchMessage() {
        return noMatchMessage;
    }

    public String succesMessage() {
        return succesMessage;
    }
}
