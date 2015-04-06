import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static java.lang.Math.abs;

public class Chess
{
	private static int SIZE = 8;
	private Piece[][] pieces = new Piece[SIZE][SIZE]; // represents the board
	private JPanel board = new JPanel();
	private char whoseTurn = 'W'; // represents whose turn it is
	private int count = 0;
	private boolean castleB = true; //ability for black to castle
	private boolean castleW = true; //ability for white to castle
	private boolean[][] castleRook = new boolean[2][2]; // corners of array represent rook in corner of board
	private Piece old = new Piece(); // for the piece clicked on to be moved
	private boolean down; //direction of piece being moved;
	private boolean right; //direction
	private Piece passant = new Piece(); // recorder for pawn that just took two steps in the prev turn
	private boolean promotion; //means pawn must be promoted
	private char chosen; //represents letter of piece the user has selected to promote their pawn

	public Chess()
	{
		//images
		ImageIcon rookB = new ImageIcon("images/B rook.png");
		ImageIcon knightB = new ImageIcon("images/B knight.png");
		ImageIcon bishopB = new ImageIcon("images/B bishop.png");
		ImageIcon queenB = new ImageIcon("images/B queen.png");
		ImageIcon kingB = new ImageIcon("images/B king.png");
		ImageIcon pawnB = new ImageIcon("images/B pawn.png");

		ImageIcon rookW = new ImageIcon("images/W rook.png");
		ImageIcon knightW = new ImageIcon("images/W knight.png");
		ImageIcon bishopW = new ImageIcon("images/W bishop.png");
		ImageIcon queenW = new ImageIcon("images/W queen.png");
		ImageIcon kingW = new ImageIcon("images/W king.png");
		ImageIcon pawnW = new ImageIcon("images/W pawn.png");

		JFrame frame = new JFrame();
		board.setLayout(new GridLayout(SIZE,SIZE));

		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				pieces[i][j] = new Piece();
				pieces[i][j].setRow(i);
				pieces[i][j].setCol(j);
			}
		}
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<2;j++)
			{
				castleRook[i][j] = true;
			}
		}
		//sets Images to pieces
		pieces[0][0].setIcon(rookB);
		pieces[0][1].setIcon(knightB);
		pieces[0][2].setIcon(bishopB);
		pieces[0][3].setIcon(queenB);
		pieces[0][4].setIcon(kingB);
		pieces[0][5].setIcon(bishopB);
		pieces[0][6].setIcon(knightB);
		pieces[0][7].setIcon(rookB);

		pieces[7][0].setIcon(rookW);
		pieces[7][1].setIcon(knightW);
		pieces[7][2].setIcon(bishopW);
		pieces[7][3].setIcon(queenW);
		pieces[7][4].setIcon(kingW);
		pieces[7][5].setIcon(bishopW);
		pieces[7][6].setIcon(knightW);
		pieces[7][7].setIcon(rookW);
		
		char[] letters = {'r','n','b','q','k','b','n','r'};
		for(int i=0;i<SIZE;i++)
		{
			pieces[1][i].setIcon(pawnB);
			pieces[1][i].setRep('p');
			pieces[1][i].setColor('B');

			pieces[0][i].setColor('B');
			pieces[0][i].setRep(letters[i]);

			pieces[6][i].setIcon(pawnW);
			pieces[6][i].setRep('p');
			pieces[6][i].setColor('W');

			pieces[7][i].setColor('W');
			pieces[7][i].setRep(letters[i]);
		}

		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				if((i+j)%2 == 0)
				{
					pieces[i][j].setBackground(Color.WHITE);
				}
				else
				{
					pieces[i][j].setBackground(Color.DARK_GRAY);
				}
				pieces[i][j].setOpaque(true); //for macs
				pieces[i][j].setBorder(null);
				board.add(pieces[i][j]);
			}
		}
		frame.add(board);
		frame.setTitle("Chess Game");
		frame.setSize(496,502);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void main(String[] args)
	{
		new Chess();
	}
	public class Piece extends JButton
	{
		private char rep = ' '; //' ' means no piece is there. n = knight, b = bishop, k = king, r = rook, q = queen, p = pawn
		private char color = ' '; //' ' means no piece is there. 'b' for black, 'w' for white;
		private int row; // records the position of the piece
		private int col; // records position

		public Piece()
		{
			addMouseListener(new MyMouseListener());
		}
		public char getRep()
		{
			return this.rep;
		}
		public void setRep(char letter)
		{
			this.rep = letter;
		}
		public char getColor()
		{
			return this.color;
		}
		public void setColor(char c)
		{
			this.color = c;
		}
		public int getRow()
		{
			return this.row;
		}
		public void setRow(int i)
		{
			this.row = i;
		}
		public int getCol()
		{
			return this.col;
		}
		public void setCol(int j)
		{
			this.col = j;
		}
		private class MyMouseListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent e)
			{
				if(count == 0 && color == whoseTurn) // for the first piece to be clicked and makes sure it is the user's piece
				{
					count++;
					old = pieces[getRow()][getCol()];
					setEnabled(false);
				}
				else if(!(isEnabled())) //if user unselected same piece
				{
					setEnabled(true);
					count = 0;
				}
				else if(count == 1 && color != whoseTurn) // if user selected a blank spot or an opponent's piece
				{
					if(legal())
					{
						copy(old);
						if(promotion)
						{
							for(int i=0;i<SIZE;i++)
							{
								for(int j=0;j<SIZE;j++)
								{
									pieces[i][j].setBorder(null); //gets rid of button look, so it looks like a chess board
								}
							}
							promote(); //dialog box so user can choose which piece to replace the pawn with
							promoting(); //changes the pawn to selected piece
						}
						if(whoseTurn == 'W') // changes the turn to the other player
						{
							whoseTurn = 'B';
						}
						else
						{
							whoseTurn = 'W';
						}
						promotion = false; //resets promotion
						count = 0; //resets count, so the user can select a piece they want to move
					}
					for(int i=0;i<SIZE;i++)
					{
						for(int j=0;j<SIZE;j++)
						{
							pieces[i][j].setBorder(null); //gets rid of button look, so it looks like a chess board
						}
					}
					down = false; //resets directions
					right = false;
				}
			}
		}
		public void copy(Piece dum) //changes 2nd selected piece to the 1st selected piece, makes first selected piece a blank
		{
			setIcon(dum.getIcon());
			setRep(dum.getRep());
			setColor(dum.getColor());
			dum.setRep(' ');
			dum.setColor(' ');
			dum.setEnabled(true);
			dum.setIcon(null);
		}
		public boolean legal()
		{
			int i = abs(old.getRow() - getRow()); //checks how many space it moves vertically
			int j = abs(old.getCol() - getCol()); //checks how many spaces it moves horizontally
			
			if(old.getRow() < getRow()) // checks horizontal direction
			{
				down = true;
			}
			if(old.getCol() < getCol()) // checks vertical direction
			{
				right = true;
			}
			switch (old.getRep()) //this switch selects the appropriate legal move checker for said type of piece
			{
				case 'r': return rook(i, j);
				case 'n': return night(i, j);
				case 'b': return bishop(i, j);
				case 'k': return king(i, j);
				case 'q': return queen(i, j);
				case 'p': return pawn(i, j);
			}
			return false;
		}
		public boolean rook(int i, int j)
		{
			if(i+j == 1) // nothing stops it from moving one square in one direction
			{
				return true;
			}
			else if(i == 0) // moves horizontally
					// the four following for loops all check if there are pieces in b/t the rook and its destination
			{
				if(right) //rook goes to the right
				{
					for(int x=old.getCol()+1;x<getCol();x++)
					{
						if(pieces[getRow()][x].getRep() != ' ')
						{
							return false;
						}
					}
				}
				else //goes to the left
				{
					for(int x=old.getCol()-1;x>getCol();x--)
					{
						if(pieces[getRow()][x].getRep() != ' ')
						{
							return false;
						}
					}
				}
			}
			else if(j == 0) //moves vertically
			{
				if(down) // goes down
				{
					for(int x=old.getRow()+1;x<getRow();x++)
					{
						if(pieces[x][getCol()].getRep() != ' ')
						{
							return false;
						}
					}
				}
				else //goes up
				{
					for(int x=old.getRow()-1;x>getRow();x--)
					{
						if(pieces[x][getCol()].getRep() != ' ')//*
						{
							return false;
						}
					}
				}
			}
			else // means the rook was not moved in just one direction
			{	
				return false;
			}
			if(castleRook[0][0] && old.getRow() == 0 && old.getCol() == 0) //checks if the top left rook was moved
			{
				castleRook[0][0] = false;
			}
			else if(castleRook[0][1] && old.getRow() == 0 && old.getCol() == 7) //checks if the top right rook was moved
			{
				castleRook[0][1] = false;
			}
			else if(castleRook[1][0] && old.getRow() == 7 && old.getCol() == 0) //checks if the bottom left rook was moved
			{
				castleRook[1][0] = false;
			}
			else if(castleRook[1][1] && old.getRow() == 7 && old.getCol() == 7) //checks if the bottom right rook was moved
			{
				castleRook[1][1] = false;
			}
			return true;
		}
		public boolean bishop(int i, int j)
		{
			if(i != j) // bishop moves diagonally, so the distance in both directions must be equal
			{
				return false;
			}
			else if(i == 1) // bishop moves one space in a diagonal direction, which is legal
			{
			}
			else if(down)  // the four following for loops check if there are any pieces in the way b/t the bishop and its destination
			{
				if(right)
				{
					for(int x=old.getRow()+1, y=old.getCol()+1;x<getRow();x++, y++)
					{
						if(pieces[x][y].getRep() != ' ')
						{
							return false;
						}
					}
				}
				else
				{
					for(int x=old.getRow()+1, y=old.getCol()-1;y>getCol();x++, y--)
					{
						if(pieces[x][y].getRep() != ' ')
						{
							return false;
						}
					}
				}
			}
			else
			{
				if(right)
				{
					for(int x=old.getRow()-1, y=old.getCol()+1;x>getRow();x--, y++)
					{
						if(pieces[x][y].getRep() != ' ')
						{
							return false;
						}
					}
				}
				else
				{
					for(int x=old.getRow()-1, y=old.getCol()-1;x>getRow();x--, y--)
					{
						if(pieces[x][y].getRep() != ' ')
						{
							return false;
						}
					}
				}
			}
			return true;
		}
		public boolean night(int i, int j)
		{
			if((i == 1 && j == 2) || (i == 2 && j == 1)) // knight jumps over pieces, it just has to move 1 space and 2 spaces in perpendicular directions
			{
				return true;
			}
			return false;
		}
		public boolean king(int i, int j)
		{
			if(i+j == 1 || (i == 1 && j == 1)) //king can move one spot in any direction
			{
				if(castleW && old.getColor() == 'W')
				{
					castleW = false; //king has moved, so castling is no longer legal for white
				}
				else if(castleB && old.getColor() == 'B')
				{
					castleB = false; //castling now illegal for black
				}
				return true;
			}
			else if(i == 0 && j == 2 && getRep() == ' ') //for castling, in which the king stays in the same row, i == 0, and moves 2 space to a blank
			{
				//the following if statements just check if any pieces are between the certain king and the certain rook and if it is legal to castle
				if(castleW && old.getColor() == 'W') 
				{
					if(right && pieces[getRow()][getCol()-1].getRep() == ' ' && pieces[7][7].getRep() == 'r') //checks if rook is there (could have been taken)
					{
						pieces[7][7].setRep(' ');
						pieces[7][7].setColor(' ');
						pieces[7][7].setEnabled(true);
						pieces[7][7].setIcon(null);

						pieces[7][5].setRep('r');
						pieces[7][5].setColor('W');
						pieces[7][5].setEnabled(true);
						pieces[7][5].setIcon(new ImageIcon("images/W rook.png"));
						System.out.println("TRUELY ROOKEDDDD");

						return true;
					}
					else if(!right && pieces[getRow()][getCol()-1].getRep() == ' ' && pieces[getRow()][getCol()+1].getRep() == ' ' && pieces[7][0].getRep() == 'r')
					{
						pieces[7][0].setRep(' ');
						pieces[7][0].setColor(' ');
						pieces[7][0].setEnabled(true);
						pieces[7][0].setIcon(null);

						pieces[7][3].setRep('r');
						pieces[7][3].setColor('W');
						pieces[7][3].setEnabled(true);
						pieces[7][3].setIcon(new ImageIcon("images/W rook.png"));

						return true;
					}
					return false;
				}
				else if(castleB && old.getColor() == 'B')
				{
					if(right && pieces[getRow()][getCol()-1].getRep() == ' ' && pieces[0][7].getRep() == 'r')
					{
						pieces[0][7].setRep(' ');
						pieces[0][7].setColor(' ');
						pieces[0][7].setEnabled(true);
						pieces[0][7].setIcon(null);

						pieces[0][5].setRep('r');
						pieces[0][5].setColor('B');
						pieces[0][5].setEnabled(true);
						pieces[0][5].setIcon(new ImageIcon("images/B rook.png"));

						return true;
					}
					else if(!right && pieces[getRow()][getCol()-1].getRep() == ' ' && pieces[getRow()][getCol()+1].getRep() == ' ' && pieces[0][0].getRep() == 'r')
					{
						pieces[0][0].setRep(' ');
						pieces[0][0].setColor(' ');
						pieces[0][0].setEnabled(true);
						pieces[0][0].setIcon(null);

						pieces[0][3].setRep('r');
						pieces[0][3].setColor('B');
						pieces[0][3].setEnabled(true);
						pieces[0][3].setIcon(new ImageIcon("images/B rook.png"));

						return true;
					}
				}
			}
			return false;
		}
		public boolean queen(int i, int j)
		{
			if(bishop(i, j) || rook(i, j)) // queen moves like a bishop and rook
						// bishop is checked first since for it to be true, i == j
			{
				return true;
			}
			return false;
		}
		public boolean pawn(int i, int j)
		{	
			if(i == 1 && j == 0 && getRep() == ' ') //if pawn moved up or down ONE square
			{
				if(old.getColor() == 'W' && !down) // checks if in the correct direction
				{
					if(getRow() == 0)
					{
						promotion = true; //reached other side, has to be promoted
					}
					return true;
				}
				else if(old.getColor() == 'B' && down) //checks if in the correct direction
				{
					if(getRow() == 7) // reached other side, has to be promoted
					{
						promotion = true;
					}
					return true;
				}
			}
			else if(i == 2 && j == 0 && getRep() == ' ') // for the pawns first initial move, checks that there is no piece there
			{
				if(old.getColor() == 'W' && !down && getRow() == 4 && pieces[getRow()+1][getCol()].getRep() == ' ') // makes sure no piece in between
				{
					passant.setRow(getRow()); //records where the piece was moved to for the possible en passant on the next turn
					passant.setCol(getCol());
					return true;
				}
				else if(old.getColor() == 'B' && down && getRow() == 3 && pieces[getRow()-1][getCol()].getRep() == ' ') //makes sure no piece in b/t
				{
					passant.setRow(getRow()); //records where the piece was moved to for the possible en passant on the next turn
					passant.setCol(getCol());
					return true;
				}	
			}
			else if(i == 1 && j == 1) //for when a pawn attempts to capture a piece
			{
				if(old.getColor() == 'W' && !down) //checks if correct direction
				{
					if(getColor() == 'B') //checks if opposing piece;
					{
						if(getRow() == 0)
						{
							promotion = true; //reached other side of board
						}
						return true;
					}
					else if(passantable()) //checks if an "en passant" is possible
					{
						pieces[passant.getRow()][passant.getCol()].setRep(' '); //sets captured pawn to a blank square
						pieces[passant.getRow()][passant.getCol()].setColor(' ');
						pieces[passant.getRow()][passant.getCol()].setIcon(null);
						passant = new Piece(); // resets the passant holder
						return true;
					}
				}
				else if(old.getColor() == 'B' && down) //checks if correct direction
				{
					if(getColor() == 'W')// checks if opposing piece
					{
						if(getRow() == 7)
						{
							promotion = true; // reached other side of board
						}
						return true;
					}
					else if(passantable()) //checks if an "en passant" is possible
					{
						pieces[passant.getRow()][passant.getCol()].setRep(' '); //sets captured pawn to a blank square
						pieces[passant.getRow()][passant.getCol()].setColor(' ');
						pieces[passant.getRow()][passant.getCol()].setIcon(null);
						passant = new Piece();
						return true;
					}
				}
			}
			return false;
		}
		public void promote()
		{
			String typed = ""; // empty string
			while(typed.length() != 1) //to ensure the user enters a char
			{
				typed = JOptionPane.showInputDialog("Type in 'n' for knight, 'b' for bishop, 'r' for rook, 'q' for queen");
			}
			char[] letter = typed.toCharArray(); //turns the 1 letter string to an array
			chosen = letter[0]; //sets the chosen char
		}
		public void promoting()
		{
			if(getColor() == 'W') //checks which color
			{
				switch(chosen) //sets the icon and representing letter for it.
				{
					case 'r': setIcon(new ImageIcon("images/W rook.png"));
						  setRep(chosen);
						  break;
					case 'n': setIcon(new ImageIcon("images/W knight.png"));
						  setRep(chosen);
						  break;
					case 'b': setIcon(new ImageIcon("images/W bishop.png"));
						  setRep(chosen);
						  break;
					case 'q': setIcon(new ImageIcon("images/W queen.png"));
						  setRep(chosen);
						  break;
				}
			}
			else
			{
				switch(chosen)
				{
					case 'r': setIcon(new ImageIcon("images/B rook.png"));
						  setRep(chosen);
						  break;
					case 'n': setIcon(new ImageIcon("images/B knight.png"));
						  setRep(chosen);
						  break;
					case 'b': setIcon(new ImageIcon("images/B bishop.png"));
						  setRep(chosen);
						  break;
					case 'q': setIcon(new ImageIcon("images/B queen.png"));
						  setRep(chosen);
						  break;
				}
			}
		}
		public boolean passantable()
		{
			if(getRep() == ' ' && old.getRow() == passant.getRow() && getCol() == passant.getCol())
			{
				//checks if passant able by seeing that the pawn and the pawn moved last turn are in the same row and will be in the same column
				return true;
			}
			return false;
		}
	}
}