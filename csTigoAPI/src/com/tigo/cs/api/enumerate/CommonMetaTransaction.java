/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tigo.cs.api.enumerate;

/**
 *
 * @author lthmii
 */
public enum CommonMetaTransaction {
 /***/
    /**Search a customer by code*/
    CLIENT_BY_CODE(1,
    "ussd.driver.metaquery.serviceprop.name.ClientCode",
    "ussd.driver.metaquery.serviceprop.message.ClientCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.ClientCode"),
    /**Search a customer by name*/
    CLIENT_BY_NAME(2,
    "ussd.driver.metaquery.serviceprop.name.ClientName",
    "ussd.driver.metaquery.serviceprop.message.ClientName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.ClientName"),
    /**Search a product by code*/
    MOTIVE_BY_CODE(3,
    "ussd.driver.metaquery.serviceprop.name.MotiveCode",
    "ussd.driver.metaquery.serviceprop.message.MotiveCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.MotiveCode"),
    /**Search a product by name*/
    MOTIVE_BY_NAME(4,
    "ussd.driver.metaquery.serviceprop.name.MotiveName",
    "ussd.driver.metaquery.serviceprop.message.MotiveName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.MotiveName"),
    /**Search a product by code*/
    PRODUCT_BY_CODE(5,
    "ussd.driver.metaquery.serviceprop.name.ProductCode",
    "ussd.driver.metaquery.serviceprop.message.ProductCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.ProductCode"),
    /**Search a product by name*/
    PRODUCT_BY_NAME(6,
    "ussd.driver.metaquery.serviceprop.name.ProductName",
    "ussd.driver.metaquery.serviceprop.message.ProductName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.ProductName"),
    /**Search a bank by code*/
    BANK_BY_CODE(7,
    "ussd.driver.metaquery.serviceprop.name.BankCode",
    "ussd.driver.metaquery.serviceprop.message.BankCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.BankCode"),
    /**Search a bank by name*/
    BANK_BY_NAME(8,
    "ussd.driver.metaquery.serviceprop.name.BankName",
    "ussd.driver.metaquery.serviceprop.message.BankName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.BankName"),
        /**Search a guard by code*/
    GUARD_BY_CODE(9,
    "ussd.driver.metaquery.serviceprop.name.GuardCode",
    "ussd.driver.metaquery.serviceprop.message.GuardCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.GuardCode"),
    /**Search a guard by name*/
    GUARD_BY_NAME(10,
    "ussd.driver.metaquery.serviceprop.name.GuardName",
    "ussd.driver.metaquery.serviceprop.message.GuardName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.GuardName"),
        /**Search a deliverer by code*/
    DELIVERER_BY_CODE(11,
    "ussd.driver.metaquery.serviceprop.name.DelivererCode",
    "ussd.driver.metaquery.serviceprop.message.DelivererCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DelivererCode"),
    /**Search a deliverer by name*/
    DELIVERER_BY_NAME(12,
    "ussd.driver.metaquery.serviceprop.name.DelivererName",
    "ussd.driver.metaquery.serviceprop.message.DelivererName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DelivererName"),
        /**Search a deposit by code*/
    DEPOSIT_BY_CODE(13,
    "ussd.driver.metaquery.serviceprop.name.DepositCode",
    "ussd.driver.metaquery.serviceprop.message.DepositCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DepositCode"),
    /**Search a deposit by name*/
    DEPOSIT_BY_NAME(14,
    "ussd.driver.metaquery.serviceprop.name.DepositName",
    "ussd.driver.metaquery.serviceprop.message.DepositName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DepositName"),
        /**Search a employee by code*/
    EMPLOYEE_BY_CODE(15,
    "ussd.driver.metaquery.serviceprop.name.EmployeeCode",
    "ussd.driver.metaquery.serviceprop.message.EmployeeCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.EmployeeCode"),
    /**Search a deposit by name*/
    EMPLOYEE_BY_NAME(16,
    "ussd.driver.metaquery.serviceprop.name.EmployeeName",
    "ussd.driver.metaquery.serviceprop.message.EmployeeName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.EmployeeName"),
        /**Search a driver by code*/
    DRIVER_BY_CODE(17,
    "ussd.driver.metaquery.serviceprop.name.DriverCode",
    "ussd.driver.metaquery.serviceprop.message.DriverCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DriverCode"),
    /**Search a deposit by name*/
    DRIVER_BY_NAME(18,
    "ussd.driver.metaquery.serviceprop.name.DriverName",
    "ussd.driver.metaquery.serviceprop.message.DriverName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.DriverName"),
        /**Search a vehicle by code*/
    VEHICLE_BY_CODE(19,
    "ussd.driver.metaquery.serviceprop.name.VehicleCode",
    "ussd.driver.metaquery.serviceprop.message.VehicleCode",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.VehicleCode"),
    /**Search a deposit by name*/
    VEHICLE_BY_NAME(20,
    "ussd.driver.metaquery.serviceprop.name.VehicleName",
    "ussd.driver.metaquery.serviceprop.message.VehicleName",
    "ussd.driver.metaquery.serviceprop.noMatchMessage.VehicleName"),
    /**Search a vehicle by code*/
    MEDIC_BY_CODE(19,
    "sms.dispatcher.metadataquery.serviceprop.name.QueryByMedicCode",
    "sms.dispatcher.metadataquery.serviceprop.message.QueryByMedicCode",
    "sms.dispatcher.metadataquery.serviceprop.nomatchmessage.QueryByMedicCode"),
    /**Search a deposit by name*/
    MEDIC_BY_NAME(20,
    "sms.dispatcher.metadataquery.serviceprop.name.QueryByMedicName",
    "sms.dispatcher.metadataquery.serviceprop.message.QueryByMedicName",
    "sms.dispatcher.metadataquery.serviceprop.nomatchmessage.QueryByMedicName"),
        /**Search a vehicle by code*/
    CLINIC_BY_CODE(19,
    "sms.dispatcher.metadataquery.serviceprop.name.QueryByClinicCode",
    "sms.dispatcher.metadataquery.serviceprop.message.QueryByClinicCode",
    "sms.dispatcher.metadataquery.serviceprop.nomatchmessage.QueryByClinicCode"),
    /**Search a deposit by name*/
    CLINIC_BY_NAME(20,
    "sms.dispatcher.metadataquery.serviceprop.name.QueryByClinicName",
    "sms.dispatcher.metadataquery.serviceprop.message.QueryByClinicName",
    "sms.dispatcher.metadataquery.serviceprop.nomatchmessage.QueryByClinicName");
    protected final int value;
    protected final String description;
    protected final String succesMessage;
    protected final String noMatchMessage;

    private CommonMetaTransaction(int value, String description, String succesMessage, String noMatchMessage) {
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
