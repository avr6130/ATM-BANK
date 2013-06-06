package com.group2.util;// Java core packages

import java.io.IOException;





public class cin  {



   public static int readInt()
   {
		String inputString = null;
        inputString = readWord( );
        return Integer.parseInt(inputString);


	}

  public static double readDouble( )

    {
        String inputString = null;
        inputString = readWord( );
        return Double.parseDouble(inputString);
    }
  public static float readFloat( )

    {
        String inputString = null;
        inputString = readWord( );
        return Float.parseFloat(inputString);
    }


   public static String readWord( )
    {
        String result = "";
        char next;

        next = readChar( );
        while (Character.isWhitespace(next))
             next = readChar( );

        while (!(Character.isWhitespace(next)))
        {
            result = result + next;
            next = readChar( );
        }

        if (next == '\r')
        {
            next = readChar( );
            if (next != '\n')
            {
                System.out.println("Fatal error in method ");
                System.exit(1);
            }
        }

        return result;
    }

      public static String readLine( )
    {
        char nextChar;
        String result = "";
        boolean done = false;

        while (!done)
        {
            nextChar = readChar( );
            if (nextChar == '\n')
               done = true;
            else if (nextChar == '\r')
            {
                //Do nothing.
                //Next loop iteration will detect '\n'.
            }
            else
               result = result + nextChar;
        }

        return result;
    }
   public static char readChar( )
    {
        int charAsInt = -1;
        try
        {
            charAsInt = System.in.read( );
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage( ));
            System.out.println("Fatal error. Ending program.");
            System.exit(0);
        }

        return (char)charAsInt;
    }

 public static char readCH( )

    {
        char inputChar = ' ';
        inputChar = readChar( );
        return (inputChar);
    }



} //end of original.cin


