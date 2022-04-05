package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{

    private String firstName;
    private String lastName;
    private List<Utilizator> friends=new ArrayList<Utilizator>();
    private String email;
    private String password;

    /**
     * Adauga un utilizator in lista de prieteni daca acesta nu este prezent
     * @param f utiliatorul adaugat
     */
    public void addFriend(Utilizator f){
        if(!friends.contains(f)) friends.add(f);
    }

    /**
     * Sterge un utilizator in lista de prieteni daca acesta este prezent
     * @param f utlizatorul sters
     */
    public void removeFriend(Utilizator f){
        if(friends.contains(f)) friends.remove(f);
    }

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Utilizator(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    /**
     * @return prenumele unui utilizator
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * seteaza prenumele unui utilizator
     * @param firstName prenumele utilizatorului
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return numele utilizatorului
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * seteaza numele  utilizatorului
     * @param lastName numele  utilizatorului
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return lista de prieteni a  utilizatorului
     */
    public List<Utilizator> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return firstName + " "+lastName;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}