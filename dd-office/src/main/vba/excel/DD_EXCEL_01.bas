Attribute VB_Name = "DD_EXCEL_01"
Option Explicit
Option Compare Text


'-------------------------------------------------------------------------
Private workingDirSet As Boolean



'-------------------------------------------------------------------------
Public Function ddOrBelow(r As Range) As Range
    Set ddOrBelow = aoUnion(r, aoBelow(r))
End Function
Public Function ddOrAbove(r As Range) As Range
    Set ddOrAbove = aoUnion(r, aoAbove(r))
End Function



'-------------------------------------------------------------------------
Public Function ddSave( _
        wb As Workbook, _
        Optional fileWithOptionalPath As String)
    On Error GoTo save_as
    
    wb.SaveAs fileWithOptionalPath
    'wb.Save
    Exit Function
save_as:
    wb.SaveAs fileWithOptionalPath
End Function



'-------------------------------------------------------------------------
Function ddSheet( _
        Optional ByVal inWorkbook As Workbook, _
        Optional ByVal name_pattern_or_index As Variant = 1, _
        Optional ByVal create_if_not_exists As Boolean = False) _
            As Worksheet
    
    If IsMissing(inWorkbook) Then
        Set inWorkbook = ActiveWorkbook
    End If
    
    Dim name As String
    name = CStr(name_pattern_or_index)
    
    Set ddSheet = aoSheetLike(inWorkbook, name)
    If ddSheet Is Nothing Then
        Dim index As Integer
        index = CInt(aoCDbl(name))
        
        If index = 0 Then
            If Trim(name) <> "0" Then
                ' failed to match sheet by given name
                
                If create_if_not_exists Then
                    Set ddSheet = _
                        aoEmptyWorksheet(inWorkbook, name)
                End If
                Exit Function
            End If
            
            index = 1
            Debug.Print "WARNING: Changing 0 to 1 for failsafety"
        End If
        
        ' -1 means last sheet, ala Perl:
        While index < 1
            index = index + inWorkbook.Sheets.Count + 1
        Wend
        
        If index <= inWorkbook.Sheets.Count Then
            Set ddSheet = inWorkbook.Sheets(index)
        End If
    End If
    
    If ddSheet Is Nothing And create_if_not_exists Then
        Set ddSheet = _
            aoEmptyWorksheet(inWorkbook, name)
    End If
End Function


'-------------------------------------------------------------------------
Public Function ddOpen( _
                 ByVal fileWithOptionalPath As String, _
        Optional ByVal allowEdit As Boolean = True, _
        Optional ByVal prompt As Boolean = True, _
        Optional ByVal create As Boolean = False) As Workbook
    Dim fileName As String
    If fileWithOptionalPath Like "*\*" Then
        fileName = right(fileWithOptionalPath, _
                            Len(fileWithOptionalPath) - _
                              InStrRev(fileWithOptionalPath, "\"))
    Else
        fileName = fileWithOptionalPath
    End If
    
    Dim dotLocation As Integer, filenameSansExtention As String
    dotLocation = InStrRev(fileName, ".")
    If dotLocation > 1 Then
        filenameSansExtention = Left(fileName, dotLocation - 1)
    End If
    
    Set ddOpen = ddScanOpenWorkbooks(fileName)
    
    If ddOpen Is Nothing Then
        Set ddOpen = aoWorkbookLike( _
                fileWithOptionalPath, allowEdit, False)
    ElseIf fileName <> fileWithOptionalPath And _
            fileWithOptionalPath <> _
              (ddOpen.Path & "\" & fileName) Then
        ddOpen.Close
        Set ddOpen = ddOpen( _
                fileWithOptionalPath, create, allowEdit, prompt)
    End If
    
    If ddOpen Is Nothing Then
        Set ddOpen = ddPrompt(fileWithOptionalPath, allowEdit)
        
        If ddOpen Is Nothing And create Then
            Set ddOpen = aoNewWorkbook(1)
            ddSave ddOpen, fileWithOptionalPath
        End If
    End If
End Function

Private Function ddPrompt( _
        message As String, _
        allowEdit As Boolean) As Workbook
    
    If Not workingDirSet Then
        ChDir ActiveWorkbook.Path
        workingDirSet = True
    End If
    
    Dim fileName As Variant
    fileName = Application.GetOpenFilename( _
                    "excel file, *.xls*; *.csv", _
                    Title:=message)
    
    If fileName = False Then
        'MsgBox ("Please select a valid file.")
    Else
        Set ddPrompt = Workbooks.Open( _
                fileName, ReadOnly:=(Not allowEdit))
    End If
End Function


'-------------------------------------------------------------------------
Private Function ddScanOpenWorkbooks(ByVal fileName As String) As Workbook
    On Error GoTo return_nothing
    
    If InStr(1, fileName, ".") = 0 Then
        Set ddScanOpenWorkbooks = ddDoScanOpenWorkbooks(fileName, "xls")
        If Not (ddScanOpenWorkbooks Is Nothing) Then Exit Function
        
        Set ddScanOpenWorkbooks = ddDoScanOpenWorkbooks(fileName, "xlsx")
        If Not (ddScanOpenWorkbooks Is Nothing) Then Exit Function
        
        Set ddScanOpenWorkbooks = ddDoScanOpenWorkbooks(fileName, "csv")
        If Not (ddScanOpenWorkbooks Is Nothing) Then Exit Function
        
    Else
        Set ddScanOpenWorkbooks = ddDoScanOpenWorkbooks(fileName)
    End If
    
    If ddScanOpenWorkbooks Is Nothing Then
        Set ddScanOpenWorkbooks = _
                aoOpenedWbLike(fileName)
    End If
    
    Exit Function
return_nothing:
    Set ddScanOpenWorkbooks = Nothing
End Function

Private Function ddDoScanOpenWorkbooks( _
                 ByVal fileNameOrPattern As String, _
        Optional ByVal extension As String = "") As Workbook
    On Error GoTo return_nothing
    
    If extension = "" Then
        Set ddDoScanOpenWorkbooks = _
            Workbooks(fileNameOrPattern)
    Else
        Set ddDoScanOpenWorkbooks = _
                Workbooks(fileNameOrPattern & "." & extension)
    End If
    
    Exit Function
return_nothing:
    Set ddDoScanOpenWorkbooks = Nothing
End Function
Private Function ddDoScanOpenWorkbooksPattern( _
        pattern As String) As Workbook
    On Error GoTo return_nothing
    
    Dim wb As Workbook
    For Each wb In Workbooks
        If wb.name Like pattern Then
            Set ddDoScanOpenWorkbooksPattern = wb
            Exit Function
        End If
    Next
    
    Exit Function
return_nothing:
    Set ddDoScanOpenWorkbooksPattern = Nothing
End Function

