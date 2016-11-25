package ru.simplgroupp.rest.api.data.user;

/**
 * Дополнительные данные пользователя для сохранения
 */
public class AdditionalData {
    /**
     * смс код для проверки
     */
    private String smsCode;

    /**
     * скан паспорта (ключ файла в хранилище)
     */
    private String passportFile;

    /**
     * скан ИНН (ключ файла в хранилище)
     */
    private String innFile;

    /**
     * скан СНИЛС (ключ файла в хранилище)
     */
    private String snilsFile;

    /**
     * скан Водительской лицензии (ключ файла в хранилище)
     */
    private String driverLicenceFile;

    /**
     * номер инн
     */
    private String inn;

    /**
     * номер снилс
     */
    private String snils;

    /**
     * владеет ли автомобилем
     */
    private Boolean car;

    /**
     * водительские права
     */
    private Boolean driverLicense;

    /**
     * скан паспорта с пропиской
     */
    private String passportRegistrationFile;


    public AdditionalData() {
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getPassportFile() {
        return passportFile;
    }

    public void setPassportFile(String passportFile) {
        this.passportFile = passportFile;
    }

    public String getInnFile() {
        return innFile;
    }

    public void setInnFile(String innFile) {
        this.innFile = innFile;
    }

    public String getSnilsFile() {
        return snilsFile;
    }

    public void setSnilsFile(String snilsFile) {
        this.snilsFile = snilsFile;
    }

    public String getDriverLicenceFile() {
        return driverLicenceFile;
    }

    public void setDriverLicenceFile(String driverLicenceFile) {
        this.driverLicenceFile = driverLicenceFile;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public Boolean getCar() {
        return car;
    }

    public void setCar(Boolean car) {
        this.car = car;
    }

    public String getPassportRegistrationFile() {
        return passportRegistrationFile;
    }

    public void setPassportRegistrationFile(String passportRegistrationFile) {
        this.passportRegistrationFile = passportRegistrationFile;
    }

    public Boolean getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(Boolean driverLicense) {
        this.driverLicense = driverLicense;
    }
}
