import java.io.*;


public class Account implements Serializable
{
	private String name;
	private int acctno;
	private int pinno;
	private double balance;
	

    Account(String Name, int acct, int pin, double bal)
    {
        name = Name;
		acctno = acct;
		pinno = pin;
		balance = bal;
		
	}

    public void print()
    {
           System.out.println("Acct# " + acctno + "\n" + "Pin# " + pinno + "\n" + "Name " + name + "\n" + "Acct Bal " + balance + "\n"  );
    }

    public String getName()
    {
		return name;
	}

	public int getID()
	{
		return acctno;
	}

	public int getPin()
	{
		return pinno;
	}

	public double getBal()
	{
		return balance;
	}

	
 public void setName(String Name)
    {
		name= new String(Name);
	}

	public void setId(int ID)
	{
		acctno=ID;
	}

	public void setPin(int Pin)
	{
		pinno=Pin;
	}

	public void setBal(double Bal)
	{
		balance=Bal;
	}

	

}
