package orion;

public class UserSession {
    private static UserSession instance;
    private long currentAccountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private double balance;
    private String profileImage;
    private String withdrawalBank;
    private String withdrawalAccount;
    private String cardType;
    private String cardBank;
    private String cardNumber;
    private String cardExpiry;
    private String cardCVV;
    private boolean isLoggedIn;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserData(long accountNumber, String firstName, String lastName, String email, 
                           String phoneNumber, double balance, String profileImage,
                           String cardType, String cardBank, String cardNumber,
                           String cardExpiry, String cardCVV) {
        this.currentAccountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.profileImage = profileImage;
        this.cardType = cardType;
        this.cardBank = cardBank;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCVV = cardCVV;
        this.isLoggedIn = true;
    }

    public void clearSession() {
        this.currentAccountNumber = 0;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
        this.balance = 0.0;
        this.profileImage = null;
        this.withdrawalBank = null;
        this.withdrawalAccount = null;
        this.cardType = null;
        this.cardBank = null;
        this.cardNumber = null;
        this.cardExpiry = null;
        this.cardCVV = null;
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public long getCurrentAccountNumber() {
        return currentAccountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getWithdrawalBank() {
        return withdrawalBank;
    }

    public String getWithdrawalAccount() {
        return withdrawalAccount;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardBank() {
        return cardBank;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setWithdrawalBank(String withdrawalBank) {
        this.withdrawalBank = withdrawalBank;
    }

    public void setWithdrawalAccount(String withdrawalAccount) {
        this.withdrawalAccount = withdrawalAccount;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setCardBank(String cardBank) {
        this.cardBank = cardBank;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }
}