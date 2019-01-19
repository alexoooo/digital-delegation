VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "CLRFileSearch"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
' By Chris Rae, 13/5/99 to 6/7/99.

Option Explicit

' Are we debugging - 3=full, 2=some 1=a little, 0=no
#Const SHOWDEBUG = 0


' -------------------------------------------------------------------------
' Object model:
'
' Methods:
'
' Execute - actually run search (returns Boolean which is always true
'           unless deletes were requested which failed). If deletes
'           were requested, the list returned from the actual search
'           has no members.
' NewSearch - clear it
'
' Properties:
'
' Lookin (string) - directory to search from
' Sort (boolean) - whether or not to sort results
' IncludeDirs (boolean) - include directories with results (not just files)
' FoundFiles (collection) - the results
' SearchSubFolders (boolean) - recurse?
' DeleteFiles (boolean) - delete files as search progresses
' DeleteFolders (boolean) - delete folders as search progresses
' -------------------------------------------------------------------------

' The directory to look in. Set using the procedures. CLR, 13/5/99.
Private priLookIn As String

' The directory to copy to. Set using the procedures. CLR, 13/5/99.
Private priCopyTo As String

' Whether or not to sort the results. CLR, 13/5/99.
Public sort As Boolean

' Whether or not to include directories in the results. CLR, 17/5/99.
Public IncludeDirs As Boolean

' The filename (well, spec) to look for. CLR, 17/5/99.
Public fileName As String

' Whether or not to search through subdirectories. CLR, 17/5/99.
Public SearchSubFolders As Boolean

' The list of results.
Public FoundFiles As New Collection

' Whether or not to delete the files. CLR, 5/7/99.
Public DeleteFiles As Boolean

' Whether or not to delete the directories. CLR, 5/7/99.
Public DeleteFolders As Boolean

' Whether everything was in fact deleted okay.
Private DeletedOkay As Boolean

Property Let lookIn(ToDir As String)
    ' Set the directory to look in. Tidies up
    ' trailing slashes. CLR, 5/7/99.

    ' Giving a base directory with a trailing slash doesn't work.
    ' This poses a little of a problem because you can't pass something
    ' like "C:\" to it. So we just chop the slash. This, however, means
    ' that you can't give it "C:" to mean the current working directory
    ' on drive C - tough. You shouldn't write code like that anyways.
    If right(ToDir, 1) = "\" Then
        ToDir = Left(ToDir, Len(ToDir) - 1)
        SDebug "Cutting trailing slash on directory name", 2
    End If

    priLookIn = ToDir
End Property
Property Get lookIn() As String
    ' Get the directory to look in. CLR, 5/7/99.
    lookIn = priLookIn
End Property

Property Let CopyTo(ToDir As String)
    ' Set the directory to copy to. Tidies up
    ' trailing slashes. CLR, 5/7/99.
    
    If right(ToDir, 1) = "\" Then
        ToDir = Left(ToDir, Len(ToDir) - 1)
        SDebug "Cutting trailing slash on directory name", 2
    End If
    
    priCopyTo = ToDir
End Property

Property Get CopyTo() As String
    ' Get the directory to copy to. CLR, 5/7/99.
    CopyTo = priCopyTo
End Property

' The routine to display debugging information. 24/5/99.
Private Sub SDebug(DBInfo As String, DebugLevel As Integer)
    #If SHOWDEBUG = 1 Then
        If DebugLevel <= 1 Then Debug.Print "(CLRFileSearch) L" & DebugLevel & " -> " & DBInfo
    #ElseIf SHOWDEBUG = 2 Then
        If DebugLevel <= 2 Then Debug.Print "(CLRFileSearch) L" & DebugLevel & " -> " & DBInfo
    #ElseIf SHOWDEBUG = 3 Then
        If DebugLevel <= 3 Then Debug.Print "(CLRFileSearch) L" & DebugLevel & " -> " & DBInfo
    #End If
End Sub

' Run when an instance of the class is started - just
' runs the clear procedure. CLR, 14/5/99.
Private Sub Class_Initialize()
    SDebug "New class instance", 1
    NewSearch
End Sub

' Clear the search. CLR, 17/5/99.
Public Sub NewSearch()
    lookIn = "c:\"
    sort = False
    IncludeDirs = False
    SearchSubFolders = True
    SDebug "Cleared search criteria", 1
    DeleteFiles = False
    DeleteFolders = False
    CopyTo = ""
    Dim i As Long
    For i = 1 To FoundFiles.Count
            FoundFiles.Remove (1)
    Next i
End Sub

' Run when the instance of the class is closed. I'm not
' 100% sure about this part.
Private Sub Class_Terminate()
    ' Kill off our results list.
    Set FoundFiles = Nothing
    SDebug "Class terminated, memory released", 1
End Sub

' The main run procedure. CLR, 13/5/99.
Public Function Execute() As Boolean
    ' Start recursing from the top dir.
    SDebug "Executing search", 1
    DeletedOkay = True
    RunDown priLookIn
    Execute = DeletedOkay
End Function

' The recursive bit. Stolen from various other programs
' I wrote with similar ends in mind. CLR, 13/5/99.
Private Sub RunDown(BaseDirectory As String)
    ' All of the files which match in the directory
    Dim FilesHere() As String
    ' And the directories
    Dim DirsHere() As String
       
    ' The count of how many files there are
    Dim FileCount As Integer
    ' And the directories
    Dim DirCount As Integer

    ' The string each filename is temporarily stored in
    Dim ThisFile As String
    ' The loop to go through each entry and perform what
    ' is necessary
    Dim AddItem As Integer
    ' The loop to recurse through each directory entry
    Dim RecurseDirs As Integer
    
    ' The flag to say whether any actual changes were made
    ' during the bubblesort
    Dim AnyChanges As Boolean
    ' The sort loop
    Dim BubbleSort As Integer
    ' The temporary swapping variable
    Dim SwapFH As String
    ' Whether or not the file is
    ' a directory
    Dim ItIsDir As Integer
    
    ' If a copy failed then don't bother trying the delete
    ' in case we lose stuff.
    Dim CopyFailed As Boolean
    
    SDebug "Searching: """ & BaseDirectory & """", 2
    
    ' Find the directories in here
    DirCount = 0
    ThisFile = Dir(BaseDirectory & "\*.*", vbDirectory)
    While ThisFile <> ""
        If ThisFile <> ".." And ThisFile <> "." Then
            ' This trap will catch if the file doesn't
            ' exist at all (occasional problem with
            ' NetWare volumes)
            On Error GoTo FileNotThere
            ' Check if it's a directory
            ItIsDir = GetAttr(BaseDirectory & "\" & ThisFile)
            If (ItIsDir And vbDirectory) Then
                SDebug "Adding dir: " & ThisFile, 3
                DirCount = DirCount + 1
                ReDim Preserve DirsHere(1 To DirCount)
                DirsHere(DirCount) = ThisFile
            End If
            GoTo SkipFNT
FileNotThere:
            ' File wouldn't read - in this case it doesn't
            ' really matter because we're just finding the
            ' directories. However, make sure it doesn't
            ' think it's a directory.
            ItIsDir = 0
            SDebug "Skipping (error): """ & BaseDirectory & "\" & ThisFile & """", 1
            On Error GoTo 0
            Resume Next
SkipFNT:
            On Error GoTo 0
        End If
        ThisFile = Dir
    Wend
    
    ' Go ahead and read all of the filenames matching the
    ' given spec into the array. Similar code to above
    ' but there ain't much we can do.
    FileCount = 0
    ThisFile = Dir(BaseDirectory & "\" & fileName, 32 + 16 + 8 + 4 + 2 + 1)
    While ThisFile <> ""
        ' Check if it's a directory. Need to force the result of
        ' the GetAttr to a boolean because otherwise it isn't and
        ' the "Not" function gets all confused. Don't ask how
        ' *!&"^£%^! long this took me to work out.
        If IncludeDirs Or Not (CBool(GetAttr(BaseDirectory & "\" & ThisFile) And vbDirectory)) Then
            FileCount = FileCount + 1
            ReDim Preserve FilesHere(1 To FileCount)
            FilesHere(FileCount) = ThisFile
        End If
        ThisFile = Dir
    Wend
    
    ' Sort the names into alphabetical order. Using a bubblesort, which
    ' seems to be fast enough at least for the moment.
    If (FileCount > 1) And sort Then
        Do
            AnyChanges = False
            For BubbleSort = 1 To FileCount - 1
                If FilesHere(BubbleSort) > FilesHere(BubbleSort + 1) Then
                    ' These two need to be swapped
                    SwapFH = FilesHere(BubbleSort)
                    FilesHere(BubbleSort) = FilesHere(BubbleSort + 1)
                    FilesHere(BubbleSort + 1) = SwapFH
                    AnyChanges = True
                End If
            Next BubbleSort
        Loop Until Not AnyChanges
    End If

    ' Create any directories necessary. This bit has to go
    ' before the file-handling section because, if directories need to be
    ' created, they need to be created before we start trying to copy files
    ' into them. Note the big lack of error-handling - the usual reason
    ' for directories not being created is because they're already there.
    ' What really matters is the file copies - if they fail, we have to
    ' be careful.
    
    ' If we're copying stuff then make the directory
    If priCopyTo <> "" Then
        SDebug "Creating dir " & priCopyTo & mid(BaseDirectory, Len(priLookIn) + 1), 2
        On Error Resume Next
        MkDir priCopyTo & mid(BaseDirectory, Len(priLookIn) + 1)
        On Error GoTo 0
    End If
    
    For AddItem = 1 To FileCount
        ' Presume pleasantly that the copy (if one happens) worked
        CopyFailed = False
        ' If we're copying the files then do that before the delete
        If priCopyTo <> "" Then
            SDebug "Writing file " & priCopyTo & mid(BaseDirectory & "\" & FilesHere(AddItem), Len(priLookIn) + 1), 2
            On Error GoTo CopyFailedErr
            FileCopy BaseDirectory & "\" & FilesHere(AddItem), priCopyTo & mid(BaseDirectory & "\" & FilesHere(AddItem), Len(priLookIn) + 1)
            GoTo SkipCopyFailed
CopyFailedErr:
            SDebug "Failed copy to " & priCopyTo & mid(BaseDirectory & "\" & FilesHere(AddItem), Len(priLookIn) + 1), 1
            CopyFailed = True
            Resume SkipCopyFailed
SkipCopyFailed:
            On Error GoTo 0
        End If
        ' If we're deleting them all then go ahead
        If DeleteFiles And Not CopyFailed Then
            SDebug "Removing file " & BaseDirectory & "\" & FilesHere(AddItem), 2
            SDebug "Clearing attributes", 3
            On Error GoTo FileNotDeleted
            SetAttr BaseDirectory & "\" & FilesHere(AddItem), 0
            SDebug "Deleting", 3
            Kill BaseDirectory & "\" & FilesHere(AddItem)
            GoTo SkipFileNotDeleted
FileNotDeleted:
            SDebug "Failed delete on " & BaseDirectory & "\" & FilesHere(AddItem), 1
            DeletedOkay = False
            Resume SkipFileNotDeleted
SkipFileNotDeleted:
            On Error GoTo 0
        Else
            ' As we're not wiping the whole thing, just
            ' add the files to the list
            FoundFiles.Add BaseDirectory & "\" & FilesHere(AddItem)
        End If
    Next AddItem
    
    ' Okay, here's the recursive bit. We now have an array full
    ' of the directory names from this particular path and we must
    ' cycle through these.
    If SearchSubFolders Then
        For RecurseDirs = 1 To DirCount
            RunDown BaseDirectory & "\" & DirsHere(RecurseDirs)
        Next RecurseDirs
    End If

    ' If we're deleting stuff then zap the directory. Remember that
    ' some files in it may have failed copies but that's okay -
    ' if they failed the copy then the file hasn't been deleted
    ' so the rmdir won't work anyway.
    If DeleteFolders Then
        SDebug "Deleting directory " & BaseDirectory, 2
        On Error GoTo DirNotDeleted
        RmDir BaseDirectory
        GoTo SkipDirNotDeleted
DirNotDeleted:
        SDebug "Failed remove on " & BaseDirectory, 1
        DeletedOkay = False
        Resume SkipDirNotDeleted
SkipDirNotDeleted:
        On Error GoTo 0
    End If

End Sub
