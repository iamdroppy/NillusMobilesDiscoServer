package com.blunk.util;

/**
 * MessageDecoding decodes the first 5characters contained at the start of each received message.
 * This class then returns the initial length of the message - useful for packet handling (Infact;
 * critical for Packet Handling!1!) * I had to change Packet to Message to stay in the FUSE style
 * Mike! * xx nils
 * 
 * @author Mike
 */
public class MessageDecoding
{
	/**
	 * Decodes the Enterprise Packet length and sets the variable "EnterprisePacketLength".
	 * 
	 * @param szChars A String[] which should only contain 5 Characters max tbh!
	 * @return EnterprisePacketLength
	 */
	public static int DecodeEnterprisePacketLength(char[] header)
	{
		int tL1 = header[4] - 128;
		int tL2 = header[3] - 128;
		int tL3 = header[2] - 128;
		
		// Calculate final length!
		int EnterprisePacketLength = (tL1 + (tL2 * 128) + (tL3 * 16384));
		
		return EnterprisePacketLength;
	}
	
	/**
	 * Decodes the Room packet length and sets the variable "RoomPacketLength".
	 * 
	 * @param szChars A String
	 * @return tReturn[] Element 0 = RoomType (Public/Private), Element 1 = RoomID
	 */
	public static String[] DecodeRoomPacket(String szChars)
	{
		String tType = new String();
		String tRoomStr = new String();
		String tDoorStr = new String();
		String tReturn[] = new String[1];
		int tRoomID = 0;
		
		if (Character.getNumericValue(szChars.charAt(0)) == 128)
		{
			tType = "private";
		}
		else
		{
			tType = "public";
		}
		
		tRoomStr = szChars.substring(2, 4);
		// I _think_ this may be the packet length!
		int tL1 = Character.getNumericValue(tRoomStr.charAt(3)) - 128;
		int tL2 = Character.getNumericValue(tRoomStr.charAt(2)) - 128;
		int tL3 = Character.getNumericValue(tRoomStr.charAt(1)) - 128;
		int tL4 = Character.getNumericValue(tRoomStr.charAt(0)) - 128;
		
		tDoorStr = szChars.substring(6, 9);
		
		int tL5 = Character.getNumericValue(tDoorStr.charAt(3)) - 128;
		int tL6 = Character.getNumericValue(tDoorStr.charAt(2)) - 128;
		int tL7 = Character.getNumericValue(tDoorStr.charAt(1)) - 128;
		int tL8 = Character.getNumericValue(tDoorStr.charAt(0)) - 128;
		
		tRoomID = (tL5 + (tL6 * 128) + (tL7 * 16384) + (tL8 * (2 ^ 21)));
		
		// Set the return!
		tReturn[0] = tType;
		tReturn[1] = Integer.toString(tRoomID);
		
		return tReturn;
	}
}
