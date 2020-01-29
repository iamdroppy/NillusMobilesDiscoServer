package net.nillus.mobilesdisco.spaces;

public class Pathfinder
{
	public static Tile calculateNextTile(int X, int Y, int goalX, int goalY)
    {
        Tile Next = new Tile(-1, -1);
        if (X > goalX && Y > goalY)
            Next = new Tile(X - 1, Y - 1);
        else if (X < goalX && Y < goalY)
            Next = new Tile(X + 1, Y + 1);
        else if (X > goalX && Y < goalY)
            Next = new Tile(X - 1, Y + 1);
        else if (X < goalX && Y > goalY)
            Next = new Tile(X + 1, Y - 1);
        else if (X > goalX)
            Next = new Tile(X - 1, Y);
        else if (X < goalX)
            Next = new Tile(X + 1, Y);
        else if (Y < goalY)
            Next = new Tile(X, Y + 1);
        else if (Y > goalY)
            Next = new Tile(X, Y - 1);

        return Next;
    }
	public static int calculateHumanRotation(int X1, int Y1, int X2, int Y2)
	{
		int Rotation = 0;
        if (X1 > X2 && Y1 > Y2)
            Rotation = 7;
        else if(X1 < X2 && Y1 < Y2)
            Rotation = 3;
        else if(X1 > X2 && Y1 < Y2)
            Rotation = 5;
        else if(X1 < X2 && Y1 > Y2)
            Rotation = 1;
        else if(X1 > X2)
            Rotation = 6;
        else if(X1 < X2)
            Rotation = 2;
        else if(Y1 < Y2)
            Rotation = 4;
        else if(Y1 > Y2)
            Rotation = 0;

        return Rotation;
	}
}
