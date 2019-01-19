VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "AO_Const"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Option Explicit


'------------------------------------------------------------
' For specifying 'match' and 'group' opperations

Public Enum Orientation
    BY_ROW
    BY_COLUMN
    BY_CELL
    AUTOMATIC
End Enum

Public Enum Corner
    TOP_LEFT
    TOP_RIGHT
    BOTTOM_LEFT
    BOTTOM_RIGHT
End Enum

Public Enum Direction
    UP
    RIGHT_HAND
    Down
    LEFT_HAND
End Enum


