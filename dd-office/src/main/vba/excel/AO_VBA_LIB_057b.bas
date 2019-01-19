Attribute VB_Name = "AO_VBA_LIB_057b"
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
''
'' !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
''
''    WRITTEN BY: ALEX OSTROVKSY
''     FOR QUESTIONS CALL 647-223-7245 OR EMAIL alex@ostrovsky.biz
''
'' !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
''


Option Explicit
Option Compare Text

Private Const SEP_STR As String = "•"
Public FileSearch As New CLRFileSearch


'---------------------------------------------------------------------
Function aoPair(ByVal a As Variant, ByVal b As Variant) As AO_Pair
    Dim instance As New AO_Pair
    
    instance.init a, b
    
    Set aoPair = instance
End Function


'---------------------------------------------------------------------
Function aoExtractRange( _
        ByVal possibleRange As Variant, Optional lookIn As Range) As Range
    
    Select Case TypeName(possibleRange)
        Case "Range"
            Set aoExtractRange = possibleRange
        
        Case "AO_Range"
            Set aoExtractRange = possibleRange.r
        
        Case "Worksheet"
            Set aoExtractRange = possibleRange.UsedRange
        
        Case "String"
            If aoIsUsable(lookIn) Then
                Set aoExtractRange = aoFind(lookIn, CStr(possibleRange))
            End If
        
        Case Else
            Set aoExtractRange = Nothing
    End Select
End Function


'---------------------------------------------------------------------
Public Function aoSheetLike( _
        wb As Workbook, _
        pattern As String) As Worksheet
        
    Dim ws As Worksheet
    
    For Each ws In wb.Sheets
        If Trim(ws.name) Like Trim(pattern) Then
            Set aoSheetLike = ws
            Exit Function
        End If
    Next
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
''   This lets me create new workbooks (with a specifiend number of sheets)
''       on-the-fly.
''       http://www.erlandsendata.no/english/vba/wb/createnewwb.hao
''
Function aoNewWorkbook(Optional ByVal wsCount As Integer = 1) As Workbook
    ' creates a new workbook with wsCount (1 to 255) worksheets
    Dim NewWorkbook As Workbook
    Dim OriginalWorksheetCount As Long
    
    Set NewWorkbook = Nothing
    
    If wsCount < 1 Or wsCount > 255 Then
        Exit Function
    End If
    
    On Error Resume Next
    OriginalWorksheetCount = Application.SheetsInNewWorkbook
    
        Application.SheetsInNewWorkbook = wsCount
        Set aoNewWorkbook = Workbooks.Add
    
    Application.SheetsInNewWorkbook = OriginalWorksheetCount
End Function


Function aoOpenWorkbook( _
        ByVal message As String, Optional ByVal allowEdit = True) As Workbook
    
    Dim fileName As Variant
    
    fileName = Application.GetOpenFilename( _
                    "excel file, *.xls*; *.csv", _
                    Title:=message)
    
    If fileName = False Then
        'MsgBox ("Please select a valid file.")
    Else
        Set aoOpenWorkbook = aoWorkbookNamed(fileName, allowEdit)
    End If
End Function

Private Function aoDoOpenWorkbook( _
        ByVal fileName As String, _
        Optional ByVal allowEdit = True) As Workbook
    Set aoDoOpenWorkbook = _
            Workbooks.Open( _
                    fileName, _
                    UpdateLinks:=0, _
                    ReadOnly:=(Not allowEdit), _
                    ignorereadonlyrecommended:=True)
End Function


Function aoWorkbookNamed( _
        ByVal pathToFile As String, _
        Optional ByVal allowEdit = True) As Workbook
    
    Dim wb As Workbook, otherWb As Workbook
    Dim fileName As String
    
    If pathToFile Like "*\*" Then
        fileName = right(pathToFile, Len(pathToFile) - InStrRev(pathToFile, "\"))
        Set wb = aoWbOrNothing(fileName)
        If wb Is Nothing Then
            Set wb = aoOpenWbOrNothing(pathToFile, allowEdit)
        End If
    Else
        fileName = pathToFile
        Set wb = aoWbOrNothing(fileName)
    End If
    
    
    If wb Is Nothing Then
        For Each otherWb In Workbooks
            Set wb = aoOpenWbOrNothing(otherWb.Path & "\" & fileName, allowEdit)
            
            If wb Is Nothing Then
                Set wb = aoOpenWbOrNothing( _
                            otherWb.Path & "\" & pathToFile, allowEdit)
            End If
            
            If Not wb Is Nothing Then
                GoTo end_of_function
            End If
        Next
        
        If wb Is Nothing Then
            Set wb = aoOpenWorkbook( _
                        "Please open the '" & pathToFile & "' file.", _
                        allowEdit)
        End If
    End If
    
end_of_function:
    Set aoWorkbookNamed = wb
End Function


Function aoWorkbookLike( _
        ByVal pattern As String, _
        Optional ByVal allowEdit As Boolean = True, _
        Optional ByVal prompt As Boolean = True) As Workbook
    
    Dim wb As Workbook, otherWb As Workbook
    
    Set wb = aoOpenedWbLike(pattern)
    If wb Is Nothing Then
        
        Dim lookinDirs As Object
        Set lookinDirs = CreateObject("Scripting.Dictionary")
        
        If aoPathOf(pattern) <> "" Then
            lookinDirs(aoPathOf(pattern)) = True
        End If
        For Each otherWb In Workbooks
            lookinDirs(otherWb.Path) = True
        Next
        
        Dim lookinDir As Variant
        For Each lookinDir In lookinDirs.keys
            Set wb = aoWbFromDirOrNothing(lookinDir, pattern, allowEdit)
            
            If Not wb Is Nothing Then
                GoTo end_of_function
            End If
        Next
        
        If prompt And (wb Is Nothing) Then
            Set wb = aoOpenWorkbook( _
                        "Please open the '" & pattern & "' file.", _
                        allowEdit)
        End If
    End If
    
end_of_function:
    Set aoWorkbookLike = wb
End Function


Function aoOpenWbOrNothing( _
        ByVal fileName As String, _
        Optional ByVal allowEdit = True) As Workbook
    
    On Error GoTo return_nothing
    
    Set aoOpenWbOrNothing = aoDoOpenWorkbook(fileName, allowEdit)
    
    Exit Function
return_nothing:
    Set aoOpenWbOrNothing = Nothing
End Function

Function aoWbOrNothing(ByVal fileName As String) As Workbook
    On Error GoTo return_nothing
    
    Set aoWbOrNothing = Workbooks(fileName & ".xls")
    
    Exit Function
return_nothing:
    Set aoWbOrNothing = Nothing
End Function

Function aoOpenedWbLike(ByVal pattern As String) As Workbook
    Dim wb As Workbook
    
    For Each wb In Workbooks
        If wb.name Like pattern Or _
                aoSansPathAndExtention(wb.name) Like pattern Then
            Set aoOpenedWbLike = wb
            Exit Function
        End If
    Next
    Set aoOpenedWbLike = Nothing
End Function

Function aoWbFromDirOrNothing( _
        ByVal directory As String, _
        ByVal pattern As String, _
        Optional ByVal allowEdit = True) As Workbook
    
    With FileSearch
        .NewSearch
        .lookIn = directory
        .fileName = "*.xls*"
        .SearchSubFolders = False
        '.FileType = msoFileTypeExcelWorkbooks
        
        If .Execute() = 0 Then
            Exit Function
        End If
        
        Dim foundFile As Variant
        For Each foundFile In .FoundFiles
            If aoSansPathAndExtention(foundFile) Like pattern Or _
                    aoSansPathAndExtention(foundFile) Like _
                        aoSansPathAndExtention(pattern) Then
                Set aoWbFromDirOrNothing = _
                        aoOpenWbOrNothing(foundFile, allowEdit)
                Exit Function
            End If
        Next
    End With
End Function

Function aoCloseWbLike( _
        ByVal pattern As String, _
        Optional saveChanges As Boolean = False) As Workbook
    Dim wb As Workbook
    
    For Each wb In Workbooks
        If wb.name Like pattern Or _
                aoSansPathAndExtention(wb.name) Like pattern Then
            wb.Close saveChanges
            Exit Function
        End If
    Next
End Function


Private Function aoSansPathAndExtention(ByVal fileName As String) As String
    Dim sansExtention As String
    Dim sansPath As String
    
    If InStr(fileName, ".") > 0 Then
        sansExtention = Left(fileName, InStrRev(fileName, ".") - 1)
    Else
        sansExtention = fileName
    End If
    
    If InStr(sansExtention, "\") > 0 Then
        sansPath = _
                right( _
                    sansExtention, _
                    Len(sansExtention) - InStrRev(sansExtention, "\"))
    Else
        sansPath = sansExtention
    End If
    
    aoSansPathAndExtention = sansPath
End Function

Private Function aoPathOf(ByVal fileName As String) As String
    If InStr(fileName, "\") > 0 Then
        aoPathOf = Left(fileName, InStrRev(fileName, "\") - 1)
    Else
        aoPathOf = ""
    End If
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Given a value as input this function determines if that value
'  is defined or not.
'
' @val    Variant  the value you want to test
' @return Boolean  if @val is defined then true, else false
'
' aoIsDefined(Null)    = False
' aoIsDefined(Empty)   = False
' aoIsDefined(Nothing) = False
'
' Anything else will return true.
'

Function aoIsDefined(ByVal val As Variant) As Boolean
    Select Case TypeName(val)
        Case "Empty"
            aoIsDefined = False
        Case "Null"
            aoIsDefined = False
        Case "Nothing"
            aoIsDefined = False
        Case Else
            aoIsDefined = True
    End Select
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Utility methods to deal with excel bugs
'

Function aoFreezePanes(at As Range) As Boolean
    Dim origSceenUpdateStatus As Boolean
    Dim origSelection As Range
    
    origSceenUpdateStatus = Application.ScreenUpdating
    Set origSelection = Selection
    
    Application.ScreenUpdating = True
    With at.Parent
        .Activate

        ActiveWindow.FreezePanes = False
        
        .cells(1, 1).Select
        at.Select
        
        If Range(at.EntireColumn.cells(1, 1), _
                  at).Height > _
                    Application.UsableHeight * 0.8 Then
            Application.ScreenUpdating = origSceenUpdateStatus
            aoSelect origSelection
            aoFreezePanes = False
            Exit Function
        End If
        
        ActiveWindow.FreezePanes = True
    End With
    Application.ScreenUpdating = origSceenUpdateStatus
    aoSelect origSelection
    aoFreezePanes = True
End Function

Function aoSelect(ByVal r As Variant)
    Dim origSceenUpdateStatus As Boolean
    Dim trueR As Range
    
    If r Is Nothing Then
        Exit Function
    Else
        Set trueR = aoExtractRange(r)
    End If
    
    'origSceenUpdateStatus = Application.ScreenUpdating
    
    Application.ScreenUpdating = True
    If ActiveSheet.name <> trueR.Parent.name Or _
           ActiveSheet.Parent.name <> _
            trueR.Parent.Parent.name Then
        trueR.Parent.Activate
    End If
    trueR.Select
    Application.ScreenUpdating = False
    
    'If Application.ScreenUpdating <> origSceenUpdateStatus Then
    '    Application.ScreenUpdating = origSceenUpdateStatus
    'End If
End Function



'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Sorts an array of values, the array that is sent in gets its elements reordered
'   so that they are in sorted order.
' The sort is stable and has O(n log n) time efficiency.
'
' @list Variant  the array that is be sorted
' @asc     Boolean  true if the values should be sorted in ascending order
'
' Dim an_array as Variant
' an_array = Array(12, 19, 2, -75, 11)
'
' aoSort(an_array)        ' an_array is now [-75, 2, 11, 12, 19]
' aoSort(an_array, false) ' an_array is now [19, 12, 11, 2, -75]
'
' comparator is a string representing a public function:
'

Function aoSort( _
        ByRef list As Variant, _
        Optional asc As Boolean = True, _
        Optional comparator As String) As Variant
    
    If IsArray(list) Then
        aoSort = aoMergeSort(list, asc, comparator)
    Else
        Set aoSort = aoMergeSort(list, asc, comparator)
    End If
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Identifies the dates in a given range.
' Date ranges with the format dateA_dateB are also identified,
'  this function also tries to filter out likely false positives.
'

Function aoDates(aRange As Variant) As Range
    Dim lookIn As Range, aCell As Range, post_ As String, asDate As Date
    Dim extracted As Range, rb As New AO_RangeBag, val As String
    
    Set extracted = aoExtractRange(aRange)
    If extracted Is Nothing Then
        Set aoDates = Nothing
        Exit Function
    End If
    
    Set lookIn = Intersect(extracted, extracted.Worksheet.UsedRange)
    
    For Each aCell In lookIn.cells 'aoFindAsIs(lookin, "*", False)
        If IsError(aCell.Value) Then
            GoTo next_cell
        End If
        val = aCell.Value
        
        If Len(val) > 5 Then
            If IsDate(val) Then
                If Not (val Like "##.###") Then
                    If InStr(aCell.NumberFormat, "y") > 0 Or _
                            InStr(aCell.NumberFormat, "m") > 0 Or _
                            InStr(aCell.NumberFormat, "d") > 0 Then
                        rb.addRange aCell
                    End If
                End If
            Else
                ' maybe its a date range
'                If TypeName(val) = "String" Then
                    If InStr(1, val, "_") > 0 Then
                        
                        post_ = right$(val, Len(val) - InStr(1, val, "_"))
                        
                        If IsDate(post_) Then
                            asDate = CDate(post_)
                            If Year(asDate) > 2000 And Year(asDate) < 2030 Then
                                rb.addRange aCell
                            End If
                        End If
                    ElseIf val Like "####.##" Then
                        If aCell.NumberFormat = "General" Then
                            rb.addRange aCell
                        End If
                    End If
                End If
'            End If
        End If
        
next_cell:
    Next
    
    Set aoDates = rb.sort
End Function



''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Given two or more Range objects, this function clumns them together into one
'  larger Range object.
' This function is better then the in-build Union function because it works as
'   extepcted if given a blank range as input (i.e. ignores it) and also guerantees
'   the order in which the ranges are clumped together (top-left to bottom-right).
'
' @range1 Variant  the first  range object we want to unite
' @range2 Variant  the second range object we want to unite
' @rangeN Variant  the N'th   range object we want to unite
' @return Range    the union of all the ranges
'
' set r  = ActiveWorksheet.UsedRange
'
' set ru = aoUnion(r.Range("B2"), r.Range("D7"))
' ru.Address = "$B$2,$D$7"
'
' set ru = aoUnion(ru, "foo")
' ru.Address = "$B$2,$D$7"
'
' set ru = aoUnion(ru, r.Range("A1"))
' ru.Address = "$A$1,$B$2,$D$7"
'

Function aoUnion(ParamArray ranges() As Variant) As Range
    Dim rb As New AO_RangeBag, oneRange As Variant
    
    For Each oneRange In ranges
        rb.Add aoExtractRange(oneRange)
    Next
    
    Set aoUnion = rb.sort
End Function

' does not guarantee that the returned range's areas are in order
Function aoUnionAsIs(ByVal rangeA As Variant, ByVal rangeB As Variant) As Range
    Dim extractedA As Range, extractedB As Range
    
    Set extractedA = aoExtractRange(rangeA)
    Set extractedB = aoExtractRange(rangeB)
    
    If Not ((extractedA Is Nothing) Or (extractedB Is Nothing)) Then
        Set aoUnionAsIs = Union(extractedA, extractedB)
    ElseIf Not (extractedA Is Nothing) Then
        Set aoUnionAsIs = extractedA
    ElseIf Not (extractedB Is Nothing) Then
        Set aoUnionAsIs = extractedB
    Else
        Set aoUnionAsIs = Nothing
    End If
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Given two or more Range objects, this functing finds the Range which is common to all
'  of them.
' This function is better then the in-build Intersect function because it works as
'   extepcted if given a blank range as input (i.e. returns nothing).
'
' @range1 Variant  the first  range object we want to interset
' @range2 Variant  the second range object we want to interset
' @rangeN Variant  the N'th   range object we want to interset
' @return Range    the intersection of all the ranges
'
' set r  = ActiveWorksheet.UsedRange
'
' set ri = aoIntersect(r.Range("1:2,5:7"), r.Range("A:C"))
' ri.Address = "$A$1:$C$2,$A$5:$C$7"
'
' set ri = aoIntersect(ri, r.Range("B:B"))
' ri.Address = "$B$1:$B$2,$B$5:$B$7"
'
' set ri = aoIntersect(ri, r.Range("G20"))
' ri is Nothing
'

Function aoIntersect(ParamArray ranges() As Variant) As Range
    Dim rb As New AO_RangeBag, oneRange As Variant
    
    rb.setConjunctive False
    
    For Each oneRange In ranges
        rb.Add oneRange
    Next
    
    Set aoIntersect = rb.sort
End Function

Function aoIntersectAsIs(ByVal range1 As Variant, ByVal range2 As Variant) As Range
    Dim r1 As Range, r2 As Range
    
    Set r1 = aoExtractRange(range1)
    Set r2 = aoExtractRange(range2)
    
    If (r1 Is Nothing) Or (r2 Is Nothing) Then
        Exit Function
    Else
        Set aoIntersectAsIs = Intersect(r1, r2)
    End If
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Example
'   set r1 = Range("3:4")
'   set r2 = Range("C:D")
'

Function aoSubtract(ByVal subtractFrom As Variant, ParamArray subtractWhat() As Variant) As Range
    Dim subFrom As Range, subWhat As New AO_RangeBag, subWhatItr As Variant
    
    Set subFrom = aoExtractRange(subtractFrom)
    
    If subFrom Is Nothing Then
        Exit Function
    End If
    
    For Each subWhatItr In subtractWhat
        subWhat.Add subWhatItr
    Next
    
    If subWhat.Flatten Is Nothing Then
        Set aoSubtract = subFrom
    Else
        Set aoSubtract = aoIntersect(subFrom, _
                                    aoCompliment(subWhat.Flatten))
    End If
End Function

'Function aoSubtract(ByVal subtractFrom As Variant, ByVal subtractWhat As Variant) As Range
'    Set subtractFrom = aoExtractRange(subtractFrom)
'    Set subtractWhat = aoExtractRange(subtractWhat)
'
'    If subtractWhat Is Nothing Then
'        Set aoSubtract = subtractFrom
'    Else
'        Set aoSubtract = aoIntersect(subtractFrom, _
'                                    aoCompliment(subtractWhat))
'    End If
'End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' The inbuilt Range.Rows (or .Columns) property behaves counter-intuitivly when
'   there is more than one Area composing a Range.
' This function returns the rows as a collection of Range objects,
'   if a row spans more than one Area then the returned Range objects
'   will reflect this (unlike the inbuilt version of this function).
' This function (unlike the inbuilt version) guerantees that the rows (or columns)
'   returned will be in a top-to-bottom..left-to-right order.
'

Function aoRows(ByVal rangeToPartition As Variant) As Collection
    Set aoRows = aoRowsOrCols(rangeToPartition, 0, True)
End Function

Function aoCols(ByVal rangeToPartition As Variant) As Collection
    Set aoCols = aoRowsOrCols(rangeToPartition, 0, False)
End Function

'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Returns only a specific row (or column) -ve indexes are counted
'   from end to beginning.
' Indexes are 1 (one) based.
'

Function aoRow(ByVal rangeToPartition As Variant, index As Long) As Range
    Set aoRow = aoRowsOrCols(rangeToPartition, index, True)
End Function

Function aoCol(ByVal rangeToPartition As Variant, index As Long) As Range
    Set aoCol = aoRowsOrCols(rangeToPartition, index, False)
End Function


Private Function aoRowsOrCols( _
        ByVal r As Variant, _
        index As Long, _
        byRow As Boolean) As Variant
    
    Dim aSpan As Range, stretchedSpans As Areas, anArea As Range
    Dim spanColl As Collection, rowsOrCols As Range, rangeToPartition As Range
    
    Set rangeToPartition = aoExtractRange(r)
    
    If rangeToPartition Is Nothing Then
        If index = 0 Then
            Set aoRowsOrCols = New Collection
        End If
        
        Exit Function
    ElseIf rangeToPartition.Areas.Count = 1 Then
        Set rowsOrCols = aoGetRowsOrCols(rangeToPartition, byRow)
        
        If index = 0 Then
            Set spanColl = New Collection
            
            For Each anArea In rowsOrCols
                spanColl.Add anArea
            Next
            
            Set aoRowsOrCols = spanColl
        ElseIf index > 0 Then
            Set aoRowsOrCols = rowsOrCols(index)
        Else
            Set aoRowsOrCols = rowsOrCols(rowsOrCols.Count + index + 1)
        End If
        
        Exit Function
    End If
    
    
    Set stretchedSpans = aoSortedEntireRowsOrCols(rangeToPartition, byRow)
    
    If index = 0 Then
        Set spanColl = New Collection
        
        For Each anArea In stretchedSpans
            For Each aSpan In aoGetRowsOrCols(anArea, byRow)
                spanColl.Add aoSortAreas(Intersect(rangeToPartition, aSpan))
            Next
        Next
        
        Set aoRowsOrCols = spanColl
        Exit Function
    Else
        Dim indexLeft As Long
        Dim loopIndex As Long, loopStart As Long, loopEnd As Long, loopStep As Long
        
        indexLeft = index
        
        If indexLeft > 0 Then
            loopStart = 1
            loopEnd = stretchedSpans.Count
            loopStep = 1
        Else
            loopStart = stretchedSpans.Count
            loopEnd = 1
            loopStep = -1
        End If
        
        For loopIndex = loopStart To loopEnd Step loopStep
            Set anArea = stretchedSpans(loopIndex)
            Set rowsOrCols = aoGetRowsOrCols(anArea, byRow)
            
            If indexLeft > 0 Then
                If indexLeft <= rowsOrCols.Count Then
                    Set aoRowsOrCols = aoSortAreas(Intersect(rangeToPartition, rowsOrCols(indexLeft)))
                    Exit Function
                Else
                    indexLeft = indexLeft - rowsOrCols.Count
                End If
            Else
                If -indexLeft <= rowsOrCols.Count Then
                    Set aoRowsOrCols = _
                            aoSortAreas(Intersect(rangeToPartition, rowsOrCols(rowsOrCols.Count + indexLeft + 1)))
                    Exit Function
                Else
                    indexLeft = indexLeft + rowsOrCols.Count
                End If
            End If
        Next
    End If
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' It is often the case that we need to identify the set (or a specific
'   member thereof) of rows that are blank (contain nothing or whitespace).
' This function lets us do just that.
'
' @bounds Variant  The range in which we look for blank rows.
'                  Defaults to ActiveWorksheet.Range
' @index  Long     If we are looking to identify a specific blank row
'                    (eg. blank row number 2 from the top) we specify this with index = 2.
'                  A positive number means count blank rows from the top.
'                  A negative number means count blank rows from the bottom.
'                  Zero means return the union of all blank rows.
'                  Defaults to 1.
'

Function aoBlankRows( _
        Optional bounds As Variant, Optional ByVal index As Long = 0) As Range
    Set aoBlankRows = aoWhitespaceRowsOrCols(True, bounds, index)
End Function



'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' It is often the case that we need to identify the set (or a specific
'   member thereof) of columns that are blank (contain nothing or whitespace).
' This function lets us do just that.
'
' @bounds Variant  The range in which we look for blank columns.
'                  Defaults to ActiveWorksheet.Range
' @index  Long     If we are looking to identify a specific blank column
'                    (eg. blank column number 2 from the left) we specify this with index = 2.
'                  A positive number means count blank columns from the left.
'                  A negative number means count blank columns from the right.
'                  Zero means return the union of all blank columns.
'                  Defaults to 1.
'

Function aoBlankCols(Optional bounds As Variant, Optional ByVal index As Long = 0) As Range
    Set aoBlankCols = aoWhitespaceRowsOrCols(False, bounds, index)
End Function

' XXX need to optimize for +ve indexing
Private Function aoWhitespaceRowsOrCols( _
        byRow As Boolean, _
        ByVal bounds As Variant, _
        Optional ByVal index As Long = 0) As Range
    
    Dim lookIn As Range, whitespace As Range, stretchedWhitespace As Range
    Dim rowsOrCols As Areas, rowOrColArea As Range, rowOrCol As Range, spanningBag As New AO_RangeBag
    Dim whitespaceInRow As Range, curRowIndex As Long
    
    Set lookIn = aoExtractRange(bounds)
    If lookIn Is Nothing Then
        Exit Function
    End If
    
    Set whitespace = aoWhitespaceCells(lookIn)
    If whitespace Is Nothing Then
        Exit Function
    Else
        Set rowsOrCols = _
                aoSortedEntireRowsOrCols( _
                    aoIntersectAsIs( _
                        whitespace, _
                        lookIn.Parent.UsedRange), _
                    byRow)
        
        For Each rowOrColArea In rowsOrCols
            For Each rowOrCol In aoGetRowsOrCols(rowOrColArea, byRow)
                Set whitespaceInRow = _
                        Intersect(lookIn, rowOrCol, whitespace)
                
                If Not (whitespaceInRow Is Nothing) Then
                    If whitespaceInRow.cells.Count = _
                            Intersect(rowOrCol, lookIn).cells.Count Then
                        curRowIndex = curRowIndex + 1
                        
                        If curRowIndex = index Then
                            Set aoWhitespaceRowsOrCols = whitespaceInRow
                            Exit Function
                        End If
                        
                        spanningBag.addRange whitespaceInRow
                    End If
                End If
            Next
        Next
        
        spanningBag.addRange _
                aoGetRowsOrCols( _
                    aoIntersectAsIs( _
                        aoCompliment_(lookIn.Parent.UsedRange), _
                        whitespace), _
                    byRow)
    End If
    
    If index = 0 Then
        Set aoWhitespaceRowsOrCols = spanningBag.sort
    Else
        Set aoWhitespaceRowsOrCols = _
                aoRowsOrCols(spanningBag.sort, index, byRow)
    End If
End Function

Private Function aoWhitespaceCells(lookIn As Range)
    Dim spaceStarting As Range
    Dim whitespaceBag As New AO_RangeBag
    Dim spaceStartingCell As Range
    
    Set spaceStarting = aoFindAsIs(lookIn, " *", False)
    If Not (spaceStarting Is Nothing) Then
        For Each spaceStartingCell In spaceStarting
            If Len(Trim$(aoCellVal(spaceStartingCell, " "))) = 0 Then
                whitespaceBag.addRange spaceStartingCell
            End If
        Next
    End If
    whitespaceBag.addRange _
            Intersect(aoCompliment_(lookIn.Parent.UsedRange), lookIn)
    whitespaceBag.addRange _
            aoSpecialCells(lookIn, xlCellTypeBlanks)
    
    Set aoWhitespaceCells = whitespaceBag.Flatten
End Function

Private Function aoSpecialCells( _
        ByVal lookIn As Range, cellType As XlCellType) As Range
    
    On Error GoTo none_found
    
    Set aoSpecialCells = _
            aoIntersect(lookIn, _
                        lookIn.SpecialCells(cellType))
    Exit Function
none_found:
    Set aoSpecialCells = Nothing
End Function
    


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Function aoTrimWs(ws As Worksheet)
    aoTrimWsEdge ws, True, True
    aoTrimWsEdge ws, True, False
    aoTrimWsEdge ws, False, True
    aoTrimWsEdge ws, False, False
End Function

Private Function aoTrimWsEdge( _
        ws As Worksheet, _
        byRow As Boolean, _
        trimHead As Boolean)
    
    Dim spans As Range, edgeSpan As Range, blankCells As Range
    Dim startIndex As Long, endIndex As Long, stepDir As Long, i As Long
    
    With ws.UsedRange
        Set spans = aoGetRowsOrCols(.cells, byRow)
        
        If trimHead Then
            stepDir = 1
            startIndex = 1
            endIndex = spans.Count
        Else
            stepDir = -1
            startIndex = spans.Count
            endIndex = 1
        End If
        
        For i = startIndex To endIndex Step stepDir
            Set edgeSpan = spans.Item(i)
            
            Set blankCells = aoWhitespaceCells(edgeSpan)
            If blankCells Is Nothing Then
                Exit For
            ElseIf blankCells.Count <> edgeSpan.cells.Count Then
                Exit For
            End If
        Next
        
        If i <> startIndex Then
            aoGetEntireRowsOrCols( _
                Range( _
                    spans.Item(startIndex), _
                    spans.Item(i - stepDir)), _
                byRow _
            ).Delete
        End If
    End With
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Returns the range that is relativly to the left of the given range.
'
'   Example:
'
'    |A|B|C|D|E|F|
'   --------------
'   1| | | | | | |
'   --------------
'   2|#|#|#|#|*| |      Given range is the starts (*)
'   --------------
'   3|#|#|#|#|*| |      The return is the hash signs (#)
'   --------------
'   4|#|#|*| |*| |
'   --------------
'   5|#|*| | | | |
'   --------------
'   6| | | | | | |
'
Function aoLeftOf(ByVal what As Variant) As Range
    Set aoLeftOf = aoMultiAreaRelativeRange(what, Direction.LEFT_HAND)
End Function

Private Function aoLeftOf_(ByVal what As Range) As Range
    Dim topLeft As Range, bottomRight As Range, bounds As Range
    
    If what.Column = 1 Then
        Exit Function
    End If
    
    Set bounds = what.Worksheet.cells
    
    Set topLeft = bounds.cells(what.Row, 1)
    Set bottomRight = _
            bounds.cells( _
                what.Row + what.rows.Count - 1, _
                what.Column - 1)

    Set aoLeftOf_ = bounds.Range(topLeft, bottomRight)
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Returns the range that is relativly to the right of the given range.
'
'   Example:
'
'    |A|B|C|D|E|F|
'   --------------
'   1| | | | | | |
'   --------------
'   2| | |*| |*|#|      Given range is the starts (*)
'   --------------
'   3| | | | | | |      The return is the hash signs (#)
'   --------------
'   4| |*|*|#|#|#|
'   --------------
'   5| |*|*|#|#|#|
'   --------------
'   6| | | | | | |
'
Function aoRightOf(ByVal what As Variant) As Range
    Set aoRightOf = aoMultiAreaRelativeRange(what, Direction.RIGHT_HAND)
End Function

Private Function aoRightOf_(ByVal what As Range) As Range
    Dim topLeft As Range, bottomRight As Range, bounds As Range
    
    Set bounds = what.Worksheet.cells
    
    If (what.Column + what.Columns.Count - 1) = _
            bounds.Columns.Count Then
        Exit Function
    End If
    
    Set topLeft = _
        bounds.cells( _
            what.Row, _
            what.Column + what.Columns.Count)
    Set bottomRight = _
            bounds.cells( _
                what.Row + what.rows.Count - 1, _
                bounds.Columns.Count)

    Set aoRightOf_ = bounds.Range(topLeft, bottomRight)
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Returns the range that is relativly above the given range.
'
'   Example:
'
'    |A|B|C|D|E|F|
'   --------------
'   1|#| |#|#|#|#|
'   --------------
'   2|#| |#|#|#|#|      Given range is the starts (*)
'   --------------
'   3|#| |*|*|*|*|      The return is the hash signs (#)
'   --------------
'   4|*| |*|*|*|*|
'   --------------
'   5| | |*|*|*|*|
'   --------------
'   6|*| | | | | |
'
Function aoAbove(ByVal what As Variant) As Range
    Set aoAbove = aoMultiAreaRelativeRange(what, Direction.UP)
End Function

Private Function aoAbove_(ByVal what As Range) As Range
    Dim topLeft As Range, bottomRight As Range, bounds As Range
    
    If what.Row = 1 Then
        Exit Function
    End If
    
    Set bounds = what.Worksheet.cells
    
    Set topLeft = bounds.cells(1, what.Column)
    Set bottomRight = _
            bounds.cells( _
                what.Row - 1, _
                what.Column + what.Columns.Count - 1)

    Set aoAbove_ = bounds.Range(topLeft, bottomRight)
End Function



'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Returns the range that is relativly below the given range.
'
'   Example:
'
'    |A|B|C|D|E|F|
'   --------------
'   1| | | | | | |
'   --------------
'   2| |*|*|*| |*|      Given range is the starts (*)
'   --------------
'   3| |*|*|*| | |      The return will be the number signs (#)
'   --------------
'   4| |#|#|#| |*|
'   --------------
'   5| |#|#|#| |#|
'   --------------
'   6| |#|#|#| |#|
'
Function aoBelow(ByVal what As Variant) As Range
    Set aoBelow = aoMultiAreaRelativeRange(what, Direction.Down)
End Function

Private Function aoBelow_(ByVal what As Range) As Range
    Dim topLeft As Range, bottomRight As Range, bounds As Range
    
    Set bounds = what.Worksheet.cells
    
    If (what.Row + what.rows.Count - 1) = bounds.rows.Count Then
        Exit Function
    End If
    
    Set topLeft = _
            bounds.cells( _
                what.Row + what.rows.Count, _
                what.Column)
    Set bottomRight = _
            bounds.cells( _
                bounds.rows.Count, _
                what.Column + what.Columns.Count - 1)

    Set aoBelow_ = bounds.Range(topLeft, bottomRight)
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Private Function aoMultiAreaRelativeRange( _
        ByVal relativeTo As Variant, _
        relation As Direction) As Range
    
    Dim r As Range
    Set r = aoExtractRange(relativeTo)
    
    If r Is Nothing Then
        Exit Function
    ElseIf r.Areas.Count = 1 Then
        Set aoMultiAreaRelativeRange = aoRelativeRange(r, relation)
        Exit Function
    End If
    
    Dim aSpan As Range, anArea As Range
    Dim findEdgeIn As Range, edge As Range, subArea As Range
    Dim comparisonGoal As Long, relationBag As New AO_RangeBag
    Dim byRow As Boolean, greaterEdge As Boolean
    Dim majorCorner As Corner
    
    If relation = Direction.LEFT_HAND Or _
            relation = Direction.RIGHT_HAND Then
        byRow = True
    Else
        byRow = False
    End If
    
    If relation = Direction.UP Or relation = Direction.LEFT_HAND Then
        comparisonGoal = 1
        majorCorner = Corner.TOP_LEFT
    Else
        comparisonGoal = -1
        majorCorner = Corner.BOTTOM_RIGHT
    End If
    
    For Each anArea In aoSortedEntireRowsOrCols(r, byRow)
        For Each aSpan In aoGetRowsOrCols(anArea, byRow)
        
            Set findEdgeIn = Intersect(r, aSpan)
            Set edge = Nothing
            
            For Each subArea In findEdgeIn
                
                If edge Is Nothing Then
                    Set edge = aoCornerOfRange(subArea, majorCorner)
                    
                ElseIf comparisonGoal = _
                        aoCompareRangePositions(subArea, edge) Then
                    
                    Set edge = _
                        aoCornerOfRange(subArea, majorCorner)
                End If
            Next
            
            relationBag.addRange aoRelativeRange(edge, relation)
        Next
    Next
    
    Set aoMultiAreaRelativeRange = relationBag.sort
End Function

Private Function aoRelativeRange( _
        r As Range, _
        relation As Direction) As Range
    
    Select Case relation
        Case Direction.UP
            Set aoRelativeRange = aoAbove_(r)
        Case Direction.RIGHT_HAND
            Set aoRelativeRange = aoRightOf_(r)
        Case Direction.Down
            Set aoRelativeRange = aoBelow_(r)
        Case Direction.LEFT_HAND
            Set aoRelativeRange = aoLeftOf_(r)
        Case Else
            Debug.Assert False
    End Select
End Function

Private Function aoGetRowsOrCols( _
        r As Range, _
        returnRows As Boolean) As Range
    
    If Not (r Is Nothing) Then
        If returnRows Then
            Set aoGetRowsOrCols = r.rows
        Else
            Set aoGetRowsOrCols = r.Columns
        End If
    End If
End Function

Private Function aoGetEntireRowsOrCols( _
        r As Range, _
        rows As Boolean) As Range
    
    If rows Then
        Set aoGetEntireRowsOrCols = r.EntireRow
    Else
        Set aoGetEntireRowsOrCols = r.EntireColumn
    End If
End Function

Private Function aoSortedEntireRowsOrCols( _
        r As Range, byRow As Boolean) As Areas
    
    Dim stretchedSpans As Range
    Dim sorted As Range
    
    If r Is Nothing Then
        Exit Function
    End If
    
    If byRow Then
        Set stretchedSpans = r.EntireRow
    Else
        Set stretchedSpans = r.EntireColumn
    End If
    
    
    Set sorted = aoSortAreas( _
                    Intersect( _
                        r.Parent.cells, _
                        stretchedSpans))
    Set aoSortedEntireRowsOrCols = sorted.Areas
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Returns the range that is all cells not part of the given range.
'
'   Example:
'
'    |A|B|C|D|E|F|
'   --------------
'   1|#|#|#|#|#|#|
'   --------------
'   2|#|#|*|*|*|#|      Given range is the starts (*)
'   --------------
'   3|#|#|#|#|#|#|      The return is the hash signs (#)
'   --------------
'   4|#|#|#|*|*|#|
'   --------------
'   5|#|#|#|#|#|#|
'   --------------
'   6|#|#|#|#|#|#|
'
Function aoCompliment(ByVal what As Variant) As Range
    Dim rb As AO_RangeBag, oneArea As Range
    Set what = aoExtractRange(what)
    
    If what Is Nothing Then
        ' should be = Everything, but that's impossible
        Set aoCompliment = Nothing
        Exit Function
    End If
    
    Set rb = New AO_RangeBag
    rb.setConjunctive False
    
    For Each oneArea In what.Areas
        rb.addRange aoCompliment_(oneArea)
    Next
    
    Set aoCompliment = rb.sort
End Function

Private Function aoCompliment_(what As Range) As Range
    If what Is Nothing Then
        Exit Function
    End If
    
    Dim rb As New AO_RangeBag
    
    rb.addRange aoAbove_(what.EntireRow)
    rb.addRange aoLeftOf_(what)
    rb.addRange aoRightOf_(what)
    rb.addRange aoBelow_(what.EntireRow)
    
    Set aoCompliment_ = rb.Flatten
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Function aoRangeCell( _
        r As Range, cellNum As Long) As Range
    
    Dim anArea As Range, cellsLeft As Long
    
    cellsLeft = cellNum
    For Each anArea In r.Areas
        cellsLeft = cellsLeft - anArea.cells.Count
        
        If cellsLeft <= 0 Then
            Set aoRangeCell = _
                    anArea.cells(anArea.cells.Count + cellsLeft)
            Exit Function
        End If
    Next
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' We often need to find a the cells in a worksheet that contain
'   certain text or that match as certain pattern.
' There is an inbuilt function that does just that, however it
'   has a few flaws which this function fixes.
' This function guerantees that the returned cells will be
'   ordered top-left to bottom-right.
' This function lets you specify wether you want a pattern match
'   or an exact match.
'
Function aoFindRegex( _
        ByVal bounds As Variant, _
        pattern As String, _
        Optional ignoreCase As Boolean = True) As Range
    
    Dim rb As New AO_RangeBag
    
    Set bounds = aoExtractRange(bounds)
    If bounds Is Nothing Then
        Exit Function
    Else
        Set bounds = aoIntersect(bounds, bounds.Parent.UsedRange)
        If bounds Is Nothing Then
            Exit Function
        End If
    End If
    
    Dim regex As Object, toTest As Range
    
    Set regex = CreateObject("vbscript.regexp")
    regex.pattern = pattern
    regex.ignoreCase = ignoreCase
    
    If Not regex.Test("") Then
        Set bounds = aoFind(bounds, "*")
        If bounds Is Nothing Then
            Exit Function
        End If
    End If
    
    For Each toTest In bounds.cells
        If Not IsError(toTest.Value) Then
            If regex.Test(toTest.Value) Then
                rb.addRange toTest
            End If
        End If
    Next
    
    Set aoFindRegex = rb.sort
End Function

Private Function aoFindAsIs( _
        ByVal bounds As Variant, _
        ByVal findLike As String, _
        findExact As Boolean) As Range
    
    Dim oneMatch As Range, firstAddress As String
    Dim rb As New AO_RangeBag, val As Variant
    
    Set bounds = aoExtractRange(bounds)
    If bounds Is Nothing Then
        Exit Function
    Else
        Set bounds = Intersect(bounds, bounds.Parent.UsedRange)
        If bounds Is Nothing Then
            Exit Function
        End If
    End If
    
    ' optimization
    If findLike = "" Then
        Set aoFindAsIs = aoSpecialCells(bounds, xlCellTypeBlanks)
        Exit Function
    End If
    
    If findExact Then
        findLike = aoEscapePat(findLike)
    End If
    
    If Strings.InStr(1, findLike, "[") > 0 Or _
            Strings.InStr(1, findLike, "#") > 0 Then
        Set aoFindAsIs = aoFindPatternUsingLoop(bounds, findLike)
        Exit Function
    End If
    
    Set oneMatch = bounds.find(findLike, lookIn:=xlValues)
    If Not (oneMatch Is Nothing) Then
        firstAddress = oneMatch.Address
        Do
            val = oneMatch.Value
            
            ' ugly because VB does not support short circuiting
            If Not IsError(oneMatch.Value) Then
                If oneMatch.Value Like findLike Then
                    rb.addRange oneMatch
                ElseIf Trim(oneMatch.Value) Like findLike Then
                    rb.addRange oneMatch
                End If
            Else
                'aoSelect oneMatch
            End If
            
            Set oneMatch = bounds.FindNext(oneMatch)
            If (oneMatch Is Nothing) Then
                Exit Do
            End If
        Loop While (Not IsError(val)) And _
                    (oneMatch.Address <> firstAddress)
    End If
    
    Set aoFindAsIs = rb.Flatten
End Function


Function aoFind( _
        ByVal bounds As Variant, _
        ByVal findLike As String, _
        Optional findExact As Boolean = False) As Range
    
    Dim r As Range
    
    Set r = aoFindAsIs(bounds, findLike, findExact)
    
    If Not (r Is Nothing) Then
        Set r = aoSortAreas(r)
    End If
    
    Set aoFind = r
End Function

Private Function aoFindPatternUsingLoop( _
        ByVal bounds As Range, _
        lookFor As String) As Range
    
    Dim oneCell As Range, firstAddress As String
    Dim rb As New AO_RangeBag
    
    If Not ("" Like lookFor) Then
        Set bounds = aoFind(bounds, "*")
        If bounds Is Nothing Then
            Exit Function
        End If
    End If
    
    For Each oneCell In bounds.cells
        If Not IsError(oneCell.Value) Then
            If Trim(oneCell.Value) Like lookFor Then
                rb.addRange oneCell
            End If
        End If
    Next
    
    Set aoFindPatternUsingLoop = rb.sort
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Returns the "bottom left", "bottom right", "top left",
'   or "top right" corner of the given Range or Worksheet object.
' Takes into account multiple areas within aRange.
'

''''''''' Nice public interface to the corner finding functions ''''
Function aoTL(ByVal aRange As Variant)
    Set aoTL = aoCornerOfMultiAreaRange(aRange, Corner.TOP_LEFT)
End Function

Function aoTR(ByVal aRange As Variant)
    Set aoTR = aoCornerOfMultiAreaRange(aRange, Corner.TOP_RIGHT)
End Function

Function aoBL(ByVal aRange As Variant)
    Set aoBL = aoCornerOfMultiAreaRange(aRange, Corner.BOTTOM_LEFT)
End Function

Function aoBR(ByVal aRange As Variant)
    Set aoBR = aoCornerOfMultiAreaRange(aRange, Corner.BOTTOM_RIGHT)
End Function

''''''''' Ugly Internals of the corner finding code ''''''''''''''''
Private Function aoCornerOfMultiAreaRange( _
        ByVal aRange As Variant, cornerIndex As Corner) As Range
    
    Dim lookIn As Range
    Set lookIn = aoExtractRange(aRange)
    
    If Not (lookIn Is Nothing) Then
        If lookIn.Areas.Count = 1 Then
            Set aoCornerOfMultiAreaRange = _
                    aoCornerOfRange(lookIn, cornerIndex)
        Else
            Set aoCornerOfMultiAreaRange = _
                    aoFindCornerClosestTo( _
                            aoFindIdealCorner(lookIn, cornerIndex), _
                            lookIn, _
                            cornerIndex)
        End If
    Else
        Set aoCornerOfMultiAreaRange = Nothing
    End If
End Function


' find a corner that is closest to the given range
Private Function aoFindCornerClosestTo( _
        ByVal idealCorner As Range, _
        ByVal Choices As Range, _
        cornerIndex As Corner) As Range
    
    Dim anArea As Range
    Dim bestCorner As Range, currentCorner As Range
    Dim distance As Long, leastDistance As Long
    
    Set bestCorner = aoCornerOfRange(Choices.Areas(1), cornerIndex)
    leastDistance = aoCalcRangeDistance(idealCorner, bestCorner)
    
    For Each anArea In Choices.Areas
        Set currentCorner = aoCornerOfRange(anArea, cornerIndex)
        distance = aoCalcRangeDistance(idealCorner, currentCorner)
        
        If distance < leastDistance Or _
                (distance = leastDistance And _
                    aoCloserToCorner( _
                        currentCorner, _
                        bestCorner, _
                        cornerIndex, _
                        Orientation.BY_ROW)) Then
            Set bestCorner = currentCorner
            leastDistance = distance
        End If
    Next
    
    Set aoFindCornerClosestTo = bestCorner
End Function


' find ideal corner to compare available corners to
Private Function aoFindIdealCorner( _
        ByVal aRange As Range, _
        ByVal cornerIndex As Corner)
    
    Dim anArea As Range, closestByRow As Range, closestByCol As Range
    Dim currentCorner As Range
    
    Set currentCorner = aoCornerOfRange(aRange.Areas(1), cornerIndex)
    Set closestByRow = currentCorner
    Set closestByCol = currentCorner
    
    For Each anArea In aRange.Areas
        Set currentCorner = aoCornerOfRange(anArea, cornerIndex)
        
        If aoCloserToCorner(currentCorner, closestByRow, _
                                cornerIndex, Orientation.BY_ROW) Then
            Set closestByRow = currentCorner
        End If
        
        If aoCloserToCorner(currentCorner, closestByCol, _
                                cornerIndex, Orientation.BY_COLUMN) Then
            Set closestByCol = currentCorner
        End If
    Next
    
    Set aoFindIdealCorner = _
            aoIntersectAsIs( _
                closestByCol.EntireColumn, _
                closestByRow.EntireRow)
End Function

' returns the corner (specified by cornerIndex) of the
'   given single area range
Private Function aoCornerOfRange( _
        ByVal aRange As Range, _
        cornerIndex As Corner) As Range
    
    Select Case cornerIndex
        Case Corner.TOP_LEFT
            Set aoCornerOfRange = _
                    aRange.cells(1, 1)
            
        Case Corner.TOP_RIGHT
            Set aoCornerOfRange = _
                    aRange.cells(1, aRange.Columns.Count)
            
        Case Corner.BOTTOM_LEFT
            Set aoCornerOfRange = _
                    aRange.cells(aRange.rows.Count, 1)
            
        Case Corner.BOTTOM_RIGHT
            Set aoCornerOfRange = _
                    aRange.cells( _
                        aRange.rows.Count, aRange.Columns.Count)
            
        Case Else
            MsgBox ("Unknown corner index " & cornerIndex)
    End Select
End Function

' returns true if r1 is closer to a corner (specified by cornerIndex) than
'  r2 (judging by orientationIndex)
Private Function aoCloserToCorner( _
        r1 As Range, _
        r2 As Range, _
        cornerIndex As Corner, _
        orientationIndex As Orientation) As Boolean
    
    ' the non-standard Select Case semantics that VB invented are really
    '   inconviniet here.
    Select Case orientationIndex
        Case Orientation.BY_ROW
            Select Case cornerIndex
                Case Corner.TOP_LEFT
                    aoCloserToCorner = r1.Row <= r2.Row
                    
                Case Corner.TOP_RIGHT
                    aoCloserToCorner = r1.Row <= r2.Row
                    
                Case Corner.BOTTOM_LEFT
                    aoCloserToCorner = r1.Row >= r2.Row
                    
                Case Corner.BOTTOM_RIGHT
                    aoCloserToCorner = r1.Row >= r2.Row
                    
                Case Else
                    MsgBox ("Unknown corner index " & cornerIndex)
            End Select
            
        Case Orientation.BY_COLUMN
            Select Case cornerIndex
                Case Corner.TOP_LEFT
                    aoCloserToCorner = r1.Column <= r2.Column
                    
                Case Corner.TOP_RIGHT
                    aoCloserToCorner = r1.Column >= r2.Column
                
                Case Corner.BOTTOM_LEFT
                    aoCloserToCorner = r1.Column <= r2.Column
                
                Case Corner.BOTTOM_RIGHT
                    aoCloserToCorner = r1.Column >= r2.Column
                    
                Case Else
                    MsgBox ("Unknown corner index " & cornerIndex)
            End Select
            
        Case Else
            MsgBox ("Unknown orientation index " & orientationIndex)
    End Select
End Function


Private Function aoCalcRangeDistance( _
        r1 As Range, r2 As Range) As Long
    aoCalcRangeDistance = _
        (r1.Row - r2.Row) ^ 2 + (r1.Column - r2.Column) ^ 2
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   Put the areas of the given range in top-to-bottom,
'        left-to-right order.
'
Function aoSortAreas(ByVal rangeToOrder As Variant) As Range
    Dim areaDict As Object, keyIndex As Long, seenRange As Boolean
    Dim oneArea As Range, keys As Variant
    Dim index As Long, prevIndex As Long, r As Range
    
    Set r = aoExtractRange(rangeToOrder)
    
    If r Is Nothing Then
        Exit Function
    ElseIf aoAreasInOrder(r) Then
        Set aoSortAreas = r
        Exit Function
    End If
    
    Set areaDict = CreateObject("Scripting.Dictionary")
    
    For Each oneArea In r.Areas
        index = 257 * oneArea.Row + oneArea.Column
        Set areaDict(index) = oneArea
    Next
    
    keys = areaDict.keys
    keys = aoSort(keys)
    
    Dim rb As New AO_RangeBag
    For keyIndex = LBound(keys) To UBound(keys)
        rb.Add areaDict(keys(keyIndex))
    Next
    
    Set aoSortAreas = rb.Flatten
End Function

' NOT part of public interface
Function aoCompareRangePositions( _
        range1 As Range, _
        range2 As Range) As Integer
    If range1.Row < range2.Row Then
        aoCompareRangePositions = 1
    ElseIf range1.Row > range2.Row Then
        aoCompareRangePositions = -1
    ElseIf range1.Column < range2.Column Then
        aoCompareRangePositions = 1
    ElseIf range1.Column > range2.Column Then
        aoCompareRangePositions = -1
    Else
        aoCompareRangePositions = 0
    End If
End Function

Function aoAreasInOrder(r As Range) As Boolean
    Dim prevIndex As Long, index As Long, anArea As Range
    
    prevIndex = -1
    For Each anArea In r.Areas
        index = 257 * anArea.Row + anArea.Column
        
        If prevIndex > index Then
            aoAreasInOrder = False
            Exit Function
        End If
        
        prevIndex = index
    Next
    
    aoAreasInOrder = True
End Function

'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' sort the given array by ascending or decending order.
'

Private Function aoMergeSort( _
        ByRef list As Variant, _
        asc As Boolean, _
        comparator As String) As Variant
    
    Dim index As Long, sorted As Boolean
    Dim lo As Long, hi As Long
    Dim isOfObject As Boolean, isArr As Boolean
    Dim asArray As Variant, asCollection As Collection
    
    isArr = IsArray(list)
    If isArr Then
        lo = LBound(list)
        hi = UBound(list)
    Else
        lo = 1
        hi = list.Count
    End If
    
    If hi - lo <= 0 Then
        If isArr Then
            aoMergeSort = list
        Else
            Set aoMergeSort = list
        End If
        Exit Function
    End If
    
    isOfObject = IsObject(list(lo))
    
    If isArr Then
        asArray = list
    Else
        Set asCollection = list
    End If
    
    'aoMergeSort_ list, lo, hi, asc, comparator
    aoMergeSort_ _
        isOfObject, isArr, asArray, asCollection, _
        lo, hi, asc, comparator
    
    If isArr Then
        aoMergeSort = asArray
    Else
        Set aoMergeSort = asCollection
    End If
End Function

Private Function aoMergeSort_( _
        isOfObject As Boolean, _
        isArr As Boolean, _
        ByRef asArray As Variant, _
        ByRef asCollection As Collection, _
        lo_ As Long, _
        hi_ As Long, _
        asc As Boolean, _
        comparator As String)
    
    Dim lo As Long, hi As Long, mid As Long, end_lo As Long
    Dim start_hi As Long, k As Long, t As Variant, cmp As Long
    
    lo = lo_
    hi = hi_
    
    If lo >= hi Then
        Exit Function
    End If
    
    
    mid = Int((lo + hi) / 2)
    
    aoMergeSort_ _
        isOfObject, isArr, asArray, asCollection, _
        lo, mid, asc, comparator
    aoMergeSort_ _
        isOfObject, isArr, asArray, asCollection, _
        mid + 1, hi, asc, comparator
    
    end_lo = mid
    start_hi = mid + 1
    
    Do While (lo <= end_lo) And (start_hi <= hi)
        If Len(comparator) = 0 Then
            cmp = aoCompareValues( _
                    aoGetIndex( _
                        lo, isOfObject, isArr, asArray, asCollection), _
                    aoGetIndex( _
                        start_hi, isOfObject, isArr, asArray, asCollection))
        Else
            cmp = Run(comparator, _
                    aoGetIndex( _
                        lo, isOfObject, isArr, asArray, asCollection), _
                    aoGetIndex( _
                        start_hi, isOfObject, isArr, asArray, asCollection))
        End If
        
        If Not asc Then
            cmp = -cmp
        End If
        
        If cmp < 0 Then
            lo = lo + 1
        Else
            If isOfObject Then
                Set t = _
                    aoGetIndex( _
                        start_hi, isOfObject, isArr, asArray, asCollection)
            Else
                t = aoGetIndex( _
                        start_hi, isOfObject, isArr, asArray, asCollection)
            End If
            
            For k = start_hi - 1 To lo Step -1
                aoAssign (k + 1), _
                        aoGetIndex( _
                            k, isOfObject, isArr, asArray, asCollection), _
                        isOfObject, isArr, asArray, asCollection
            Next
            aoAssign lo, t, isOfObject, isArr, asArray, asCollection

            lo = lo + 1
            end_lo = end_lo + 1
            start_hi = start_hi + 1
        End If
    Loop
End Function

Private Function aoGetIndex( _
        index As Long, _
        isOfObject As Boolean, _
        isArr As Boolean, _
        ByRef asArray As Variant, _
        ByRef asCollection As Collection) As Variant
    
    If isOfObject Then
        If isArr Then
            Set aoGetIndex = asArray(index)
        Else
            Set aoGetIndex = asCollection(index)
        End If
    Else
        If isArr Then
            aoGetIndex = asArray(index)
        Else
            aoGetIndex = asCollection(index)
        End If
    End If
End Function

' why does it have to be so difficult to assign a value?!!
Private Function aoAssign( _
        index As Long, _
        ByRef val As Variant, _
        isOfObject As Boolean, _
        isArr As Boolean, _
        ByRef asArray As Variant, _
        ByRef asCollection As Collection)
    Dim bugfix As Collection
    
    If isArr Then
        If isOfObject Then
            Set asArray(index) = val
        Else
            asArray(index) = val
        End If
    Else
        asCollection.Add val, before:=index
        asCollection.Remove (index + 1)
    End If
End Function

Function aoCompareValues(val1 As Variant, val2 As Variant) As Long
    If IsNumeric(val1) And IsNumeric(val2) Then
        If CDbl(val1) < CDbl(val2) Then
            aoCompareValues = -1
        ElseIf CDbl(val1) > CDbl(val2) Then
            aoCompareValues = 1
        Else
            aoCompareValues = 0
        End If
    Else
        If val1 Like "*#*" And val2 Like "*#*" Then
            Dim index As Long, c1 As String, c2 As String
            For index = 1 To WorksheetFunction.Min(Len(val1), Len(val2))
                c1 = mid(val1, index, 1)
                c2 = mid(val2, index, 1)
                
                If c1 Like "#" And c2 Like "#" Then
                    aoCompareValues = _
                        aoCompareByNumberPrefix( _
                            mid(val1, index), mid(val2, index))
                    Exit Function
                ElseIf c1 Like "#" Then
                    aoCompareValues = -1
                    Exit Function
                ElseIf c2 Like "#" Then
                    aoCompareValues = 1
                    Exit Function
                ElseIf c1 <> c2 Then
                    If c1 < c1 Then
                        aoCompareValues = -1
                    Else
                        aoCompareValues = 1
                    End If
                    Exit Function
                End If
            Next
        End If
        
        If val1 < val2 Then
            aoCompareValues = -1
        ElseIf val1 > val2 Then
            aoCompareValues = 1
        Else
            aoCompareValues = 0
        End If
    End If
End Function

Private Function aoCompareByNumberPrefix( _
        val1 As String, val2 As String) As Long
    Dim num1 As String, num2 As String
    Dim index As Long, c1 As String, c2 As String
    For index = 1 To WorksheetFunction.Min(Len(val1), Len(val2))
        c1 = mid(val1, index, 1)
        c2 = mid(val2, index, 1)
        
        If c1 Like "#" And c2 Like "#" Then
            num1 = num1 & c1
            num2 = num2 & c2
        ElseIf c1 Like "#" Then
            aoCompareByNumberPrefix = 1
            Exit Function
        ElseIf c2 Like "#" Then
            aoCompareByNumberPrefix = -1
            Exit Function
        Else
            Exit For
        End If
    Next
    
    If Len(val1) <> Len(val2) Then
        If Len(val1) > Len(val2) Then
            If mid(val1, index, 1) Like "#" Then
                aoCompareByNumberPrefix = 1
                Exit Function
            End If
        Else
            If mid(val2, index, 1) Like "#" Then
                aoCompareByNumberPrefix = -1
                Exit Function
            End If
        End If
    End If
    If CInt(num1) < CInt(num2) Then
        aoCompareByNumberPrefix = -1
    ElseIf CInt(num1) > CInt(num2) Then
        aoCompareByNumberPrefix = 1
    ElseIf index = Len(val1) Then
        aoCompareByNumberPrefix = -1
    ElseIf index = Len(val2) Then
        aoCompareByNumberPrefix = 1
    Else
        aoCompareByNumberPrefix = _
            aoCompareValues( _
                mid(val1, index + 1), mid(val2, index + 1))
    End If
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Given a pattern (the thing you use with the Like operator) this
'   function returns the literal vertion of it (i.e. makes it not
'    be a pattern anymore).
'
Private Function aoEscapePat(ByVal pattern As String) As String
    Dim i As Integer, buffer() As Byte
    Dim char As String

    aoEscapePat = ""
    For i = 0 To Len(pattern) - 1
        char = mid$(pattern, i + 1, 1)
        If char = "[" Or char = "#" Or char = "*" Or char = "?" Then
            aoEscapePat = aoEscapePat & "[" & char & "]"
        Else
            aoEscapePat = aoEscapePat & char
        End If
    Next i
End Function




''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'   When using a pivot table a stair-like set of values occures (as
'       in the illustration below).  The next few rutines let you
'       get the proper values from this stair structure as if it
'       were entriely filled out.
'
'   The procedure is as follows:
'       Stair Fill Procedure:
'           (1) Given a rectangle to look at.
'           (2) Look only at the 1st column.
'           (3) Copy down the current value (starting at top left cell)
'                   until the current value is some non-blank
'                   value differant from the one being copied.
'           (4) For take the section of the given rectagle in which
'                   we just finished copying down the value and
'                   pass it to Stair Fill Procedure
'
'       Stair Value for a Given Cell C:
'           (1) Go to the lefaoost cell of the row that contains C.
'           (2) Go up until the values changes (from non-blank to some
'                   other non-blank), call the last cell that had a
'                   consistant value TL.
'           (3) Perform "Stair Fill Procedure" on rectangle (TL, C).
'           (4) Return the last value found by "Stair Fill Procedure"
'
'
'   Given bounds is:
'    |A|B|                               |A|B|
'   ------                              ------
'   1|a| |                              1|a| |
'   ------                              ------
'   2| |a|                              2|a|a|
'   ------                              ------
'   3|b|b|                              3|b|b|
'   ------                              ------
'   4| |c|                              4|b|c|
'   ------                     |\       ------
'   5|c| |   -------------------  \     5|c| |
'   ------    IS  TRANSFORMED  TO   >   ------
'   6| | |   -------------------  /     6|c| |
'                              |/
'

Private Function aoExtractLowestStairSpan( _
        bounds As Range, minorDirection As Direction) As Range
    
    Dim currentCellIndex As Long
    Dim currentCell As Range, lastNonBlankCell As Range
    Dim currentValue As String, lastNonBlankValue As String
    
    For currentCellIndex = bounds.cells.Count To 1 Step -1
    
        Set currentCell = bounds.cells(currentCellIndex)
        currentValue = Trim(aoCellVal(currentCell))
        
        If currentValue <> "" Then
        
            If lastNonBlankValue = "" Then
                lastNonBlankValue = currentValue
                Set lastNonBlankCell = currentCell
                
            ElseIf currentValue = lastNonBlankValue Then
                Set lastNonBlankCell = currentCell
                
            Else
                Exit For
            End If
            
        End If
    Next
    
    If lastNonBlankCell Is Nothing Then
        Set aoExtractLowestStairSpan = bounds
    Else
        Set aoExtractLowestStairSpan = _
                aoUnionAsIs( _
                    lastNonBlankCell, _
                    aoIntersectAsIs( _
                        bounds, _
                        aoRelativeRange( _
                            lastNonBlankCell, _
                            aoOppositDirection(minorDirection)) _
                    ) _
                )
    End If
End Function

Private Function aoReducingStairValFinder( _
        bounds As Range, _
        isVertical As Boolean, _
        minorDirection As Direction, _
        majorDirection As Direction) As String
    
    Dim leastMinorStairSpan As Range
    Dim rowsOrCols As Range
    
    Set rowsOrCols = aoGetRowsOrCols(bounds, Not isVertical)
    Set leastMinorStairSpan = _
            aoExtractLowestStairSpan(rowsOrCols(1), minorDirection)
    
    If rowsOrCols.Count = 1 Then
        aoReducingStairValFinder = _
                Trim(aoCellVal(leastMinorStairSpan.cells(1, 1)))
    Else
        aoReducingStairValFinder = _
            aoReducingStairValFinder( _
                aoIntersectAsIs( _
                    bounds, _
                    aoRelativeRange( _
                        leastMinorStairSpan, _
                        aoOppositDirection(majorDirection)) _
                ), _
                isVertical, minorDirection, majorDirection _
            )
    End If
End Function

Function aoStairVal( _
        ByVal bounds As Variant, _
        aCell As Range, _
        Optional isVertical As Boolean = True) As String
    
    Dim lookIn As Range, lookat As Range
    Dim majorDirection As Direction, minorDirection As Direction
    
    If isVertical Then
        majorDirection = Direction.LEFT_HAND
        minorDirection = Direction.UP
    Else
        majorDirection = Direction.UP
        minorDirection = Direction.LEFT_HAND
    End If
    
    Set lookIn = aoExtractRange(bounds)
    If lookIn Is Nothing Then
        Exit Function
    Else
        Set lookIn = Union(lookIn, lookIn)
        Set lookat = aoIntersectAsIs(lookIn, aCell)
        
        Debug.Assert Not (lookat Is Nothing)
        Debug.Assert lookat.cells.Count = 1
        
        If lookIn.Areas.Count > 1 Then
            Dim anArea As Range
            
            For Each anArea In lookIn.Areas
                If Not (Intersect(anArea, lookat) Is Nothing) Then
                    Set lookIn = anArea
                    Exit For
                End If
            Next
        End If
    End If
    
    If Len(aoCellVal(lookat)) = 0 Then
        If isVertical Then
            aoStairVal = _
                aoCachedStairVal( _
                    lookIn, _
                    aCell.Offset(-1, 0), isVertical)
        Else
            aoStairVal = _
                aoCachedStairVal( _
                    lookIn, _
                    aCell.Offset(0, -1), isVertical)
        End If
        
        If Len(aoStairVal) = 0 Then
            aoStairVal = _
                aoReducingStairValFinder( _
                    Range(lookIn.cells(1, 1), lookat), _
                    isVertical, minorDirection, majorDirection)
        End If
    Else
        aoStairVal = aoCellVal(lookat)
    End If
    
    aoStairVal = Trim(aoStairVal)
    aoCachedStairVal lookIn, lookat, isVertical, aoStairVal
End Function

Private Function aoOppositDirection(d As Direction) As Direction
    Select Case d
        Case Direction.UP
            aoOppositDirection = Direction.Down
        Case Direction.Down
            aoOppositDirection = Direction.UP
        Case Direction.LEFT_HAND
            aoOppositDirection = Direction.RIGHT_HAND
        Case Direction.RIGHT_HAND
            aoOppositDirection = Direction.LEFT_HAND
        Case Else
            Debug.Assert False
    End Select
End Function


Private Function aoClearStairValCache()
    aoCachedStairVal Nothing, Nothing, False
End Function

' cache:   {sheet & bounds & isVertical => {majorSpanOfCell =>[cell 1, cell 2, ...]} }
Private Function aoCachedStairVal( _
        bounds As Range, _
        aCell As Range, _
        isVertical As Boolean, _
        Optional val As String) As String
    
    Static cache As Object
    Static cacheMajorSpan As Range

    Dim majorSpanKey As String
    Dim majorSpanOfCell As Range, minorIndexOfCell As Long

    If cache Is Nothing Then
        Set cache = CreateObject("Scripting.Dictionary")
    End If

    If bounds Is Nothing Then
        Set cacheMajorSpan = Nothing
        cache.RemoveAll
        Exit Function
    End If

    Set majorSpanOfCell = _
            Intersect(bounds, aoGetEntireRowsOrCols(aCell, isVertical))
    If majorSpanOfCell Is Nothing Then
        Exit Function
    End If
    
    ' this is really ugly because VB does not support short circuiting
    If aoIsUsable(cacheMajorSpan) Then
        If cacheMajorSpan.Parent.name = majorSpanOfCell.Parent.name Then
            If cacheMajorSpan.Address <> majorSpanOfCell.Address Then
                If isVertical Then
                    If majorSpanOfCell.Row = 1 Then
                        cache.RemoveAll
                    ElseIf cacheMajorSpan.Address <> _
                            majorSpanOfCell.Offset(-1, 0).Address Then
                        cache.RemoveAll
                    End If
                Else
                    If majorSpanOfCell.Column = 1 Then
                        cache.RemoveAll
                    ElseIf cacheMajorSpan.Address <> _
                            majorSpanOfCell.Offset(0, -1).Address Then
                        cache.RemoveAll
                    End If
                End If
            End If
        Else
            cache.RemoveAll
        End If
    Else
        cache.RemoveAll
    End If
    Set cacheMajorSpan = majorSpanOfCell
    
    If isVertical Then
        minorIndexOfCell = aCell.Column
    Else
        minorIndexOfCell = aCell.Row
    End If
    
    If Len(val) = 0 Then
        If cache.exists(minorIndexOfCell) Then
            aoCachedStairVal = cache(minorIndexOfCell)
        Else
            aoCachedStairVal = vbNullString
        End If
    Else
        cache(minorIndexOfCell) = val
    End If
End Function

' checking if not (r is nothing) is
'   sometimes not enaugh.
Public Function aoIsUsable(r As Range) As Boolean
    On Error GoTo error_occured
    
    If r Is Nothing Then
        aoIsUsable = False
    Else
        r.cells(1, 1).Value
        aoIsUsable = True
    End If
    
    Exit Function
error_occured:
    aoIsUsable = False
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Function aoFillStair( _
        ByVal bounds As Variant, _
        Optional isVertical As Boolean = True) As Boolean
    
    Dim lookIn As Range
    Dim valChanged As Boolean
    
    Set lookIn = aoExtractRange(bounds)
    
    aoClearStairValCache
    
    If lookIn Is Nothing Then
        Exit Function
    ElseIf lookIn.Areas.Count > 1 Then
        MsgBox "Cannot handle multi-area stair opperations."
        Debug.Assert False
        Exit Function
    Else
        Set lookIn = aoIntersectAsIs(lookIn, lookIn.Parent.UsedRange)
        
        If Not (lookIn Is Nothing) Then
            valChanged = aoFillSubStair(lookIn, isVertical)
        End If
    End If
    
    aoClearStairValCache
    aoFillStair = valChanged
End Function

Private Function aoFillSubStair( _
        bounds As Range, isVertical As Boolean) As Boolean
    
    Dim valChanged As Boolean, subValChanged As Boolean
    Dim rowsOrCols As Range, firstRowOrCol As Range
    Dim aCell As Range, cellIndex As Long, cellVal As String
    Dim firstOfBlankSpan As Range, spanCovered As Range
    
    Set rowsOrCols = aoGetRowsOrCols(bounds, Not isVertical)
    Set firstRowOrCol = rowsOrCols(1)
    
    For cellIndex = firstRowOrCol.cells.Count To 1 Step -1
        Set aCell = firstRowOrCol.cells(cellIndex)
        cellVal = aCell
        
        If (firstOfBlankSpan Is Nothing) And Len(cellVal) = 0 Then
            Set firstOfBlankSpan = aCell
        End If
        
        If Len(cellVal) > 0 Then
            If firstOfBlankSpan Is Nothing Then
                Set spanCovered = aCell
            Else
                Set spanCovered = Range(firstOfBlankSpan, aCell)
                spanCovered.Value = cellVal
                valChanged = True
            End If
            
            If rowsOrCols.Count > 1 Then
                If isVertical Then
                    subValChanged = _
                        aoFillSubStair( _
                            Intersect( _
                                bounds, aoRightOf_(spanCovered)), _
                            isVertical)
                Else
                    subValChanged = _
                        aoFillSubStair( _
                            Intersect(bounds, aoAbove_(spanCovered)), _
                            isVertical)
                End If
                
                valChanged = (valChanged Or subValChanged)
            End If
            
            Set firstOfBlankSpan = Nothing
        End If
    Next
    
    aoFillSubStair = valChanged
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Function aoUnfillStair( _
        ByVal bounds As Variant, _
        Optional isVertical As Boolean = True)
    Dim lookIn As Range
    Set lookIn = aoExtractRange(bounds)
    
    aoClearStairValCache
    
    If lookIn Is Nothing Then
        Exit Function
    ElseIf lookIn.Areas.Count > 1 Then
        MsgBox "Cannot handle multi-area stair opperations."
        Debug.Assert False
        Exit Function
    Else
        Set lookIn = _
                aoIntersectAsIs(lookIn, _
                    lookIn.Parent.UsedRange)
        If Not (lookIn Is Nothing) Then
            aoUnfillSubStair lookIn, isVertical
        End If
    End If
    
    aoClearStairValCache
End Function

Private Function aoUnfillSubStair( _
        bounds As Range, isVertical As Boolean)
    
    Dim aCell As Range
    Dim curVal As String, prevVal As String
    Dim spanStart As Range, prevCell As Range
    Dim majorRowsOrCols As Range, isFirstItr As Boolean
    
    Set majorRowsOrCols = aoGetRowsOrCols(bounds, Not isVertical)
    
    isFirstItr = True
    Set spanStart = bounds.cells(1, 1)
    
    For Each aCell In majorRowsOrCols(1).cells
        curVal = aoCellVal(aCell)
        
        If Not isFirstItr Then
            If Len(curVal) > 0 And curVal <> prevVal Then
                If isVertical Then
                    If spanStart.Row < prevCell.Row Then
                        Range( _
                            spanStart.Offset(1, 0), _
                            prevCell).Value = ""
                    End If
                Else
                    If spanStart.Column < prevCell.Column Then
                        Range( _
                            spanStart.Offset(0, 1), _
                            prevCell).Value = ""
                    End If
                End If
                
                If majorRowsOrCols.Count > 1 Then
                    If isVertical Then
                        aoUnfillSubStair _
                            Range( _
                                spanStart.Offset(0, 1), _
                                prevCell.Offset( _
                                    0, majorRowsOrCols.Count - 1)), _
                            isVertical
                    Else
                        aoUnfillSubStair _
                            Range( _
                                spanStart.Offset(1, 0), _
                                prevCell.Offset( _
                                    majorRowsOrCols.Count - 1, 0)), _
                            isVertical
                    End If
                End If
                
                Set spanStart = aCell
            End If
        Else
            isFirstItr = False
        End If
        
        prevVal = curVal
        Set prevCell = aCell
    Next
    
    ' XXX: DUPE FROM ABOVE, NEED TO REFACTOR
    If Not (prevCell Is Nothing) Then
        If isVertical Then
            If spanStart.Row < prevCell.Row Then
                Range(spanStart.Offset(1, 0), prevCell).Value = ""
            End If
        Else
            If spanStart.Column < prevCell.Column Then
                Range(spanStart.Offset(0, 1), prevCell).Value = ""
            End If
        End If
        
        If majorRowsOrCols.Count > 1 Then
            If isVertical Then
                aoUnfillSubStair _
                    Range( _
                        spanStart.Offset(0, 1), _
                        prevCell.Offset( _
                            0, majorRowsOrCols.Count - 1)), _
                    isVertical
            Else
                aoUnfillSubStair _
                    Range( _
                        spanStart.Offset(1, 0), _
                        prevCell.Offset( _
                            majorRowsOrCols.Count - 1, 0)), _
                    isVertical
            End If
        End If
    End If
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Function aoFormatStair( _
        ByVal bounds As Variant, _
        Optional isVertical As Boolean = True)
    
    Dim lookIn As Range, longerRowsOrCols As Range
    
    Set lookIn = aoExtractRange(bounds)
    
    aoClearStairValCache
    If lookIn Is Nothing Then
        Exit Function
    Else
        Set lookIn = aoIntersect(lookIn, lookIn.Parent.UsedRange)
        
        If lookIn Is Nothing Then
            Exit Function
        End If
    End If
    
    lookIn.Borders(xlInsideVertical).LineStyle = xlNone
    lookIn.Borders(xlInsideHorizontal).LineStyle = xlNone
    
    Set longerRowsOrCols = aoGetRowsOrCols(lookIn, Not isVertical)
    If longerRowsOrCols.Count > 1 Then
        aoFormat _
                Range( _
                    longerRowsOrCols(1), _
                    longerRowsOrCols(longerRowsOrCols.Count - 1)), _
                interiorColor:=2, forceInterior:=False
    End If
    
    If isVertical Then
        aoFormatStairBordersForPart lookIn, isVertical, _
                xlEdgeLeft, xlEdgeBottom, Down, RIGHT_HAND
    Else
        aoFormatStairBordersForPart lookIn, isVertical, _
                xlEdgeTop, xlEdgeRight, RIGHT_HAND, Down
    End If
    
    aoClearStairValCache
End Function

Private Function aoFormatStairBordersForPart( _
        part As Range, isVertical As Boolean, _
        longBorder As XlBordersIndex, shortBorder As XlBordersIndex, _
        longDirection As Direction, shortDirection As Direction)
    
    Dim partCell As Range, prevPertCell As Range, firstOfSpan As Range
    Dim currVal As String, prevVal As String
    Dim longSpans As Range
    
    Set longSpans = aoGetRowsOrCols(part, Not isVertical)
    Set firstOfSpan = longSpans(1).cells(1, 1)
    prevVal = Trim(aoCellVal(firstOfSpan))
    
    For Each partCell In longSpans(1).cells
        If Len(aoCellVal(partCell)) > 0 Then
            currVal = aoStairVal(part, partCell, isVertical)
        End If
        
        If currVal <> prevVal Then
            If longSpans.Count > 1 Then
                If prevPertCell.Borders(shortBorder).LineStyle <> _
                        xlContinuous Then
                    
                    Intersect( _
                        aoGetEntireRowsOrCols( _
                            prevPertCell, isVertical), _
                        part) _
                        .Borders(shortBorder).LineStyle = xlContinuous
                End If
            End If
            
            If Len(prevVal) > 0 Then
                If (isVertical And firstOfSpan.Column > 1) Or _
                        ((Not isVertical) And firstOfSpan.Row > 1) Then
                    Range(firstOfSpan, prevPertCell) _
                        .Borders(longBorder).LineStyle = xlContinuous
                End If
                
                If longSpans.Count > 1 Then
                    aoFormatStairBordersForPart _
                            Intersect(part, _
                                aoRelativeRange( _
                                    Range(firstOfSpan, prevPertCell), _
                                    shortDirection)), _
                            isVertical, _
                            longBorder, _
                            shortBorder, _
                            longDirection, _
                            shortDirection
                End If
            End If
            
            Set firstOfSpan = partCell
        End If
        
        prevVal = currVal
        Set prevPertCell = partCell
    Next
    
    ' XXX copy from above, need to refactor
    If longSpans.Count > 1 Then
        If prevPertCell.Borders(shortBorder).LineStyle <> _
                xlContinuous Then
            Intersect( _
                aoGetEntireRowsOrCols( _
                    prevPertCell, isVertical), _
                part) _
                .Borders(shortBorder).LineStyle = xlContinuous
        End If
    End If
    
    If Len(prevVal) > 0 Then
        If (isVertical And firstOfSpan.Column > 1) Or _
                ((Not isVertical) And firstOfSpan.Row > 1) Then
            Range(firstOfSpan, prevPertCell) _
                .Borders(longBorder).LineStyle = xlContinuous
        End If
            
        If longSpans.Count > 1 Then
            If currVal = prevVal Then
                aoFormatStairBordersForPart _
                        Intersect(part, _
                            aoRelativeRange( _
                                Range(firstOfSpan, prevPertCell), _
                                shortDirection)), _
                        isVertical, _
                        longBorder, _
                        shortBorder, _
                        longDirection, _
                        shortDirection
            End If
        End If
    End If
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Clears an existing, or creates a new worksheet (and returns it)
'   from the specified workbook.
'

Function aoEmptyWorksheet( _
        wb As Workbook, name As String, _
        Optional deleteCells As Boolean = True, _
        Optional positionFirst As Boolean = False) As Worksheet
    
    Dim index As Long, ws As Worksheet, alreadyExists As Boolean
    
    For index = 1 To wb.Sheets.Count
        If UCase(name) = UCase(wb.Sheets(index).name) Then
            alreadyExists = True
            Exit For
        End If
    Next
    
    If alreadyExists Then
        Set ws = wb.Sheets(name)
        If deleteCells Then
            ws.UsedRange.EntireColumn.Delete
            ws.UsedRange.EntireRow.Delete
            
            ws.Activate
            ActiveWindow.FreezePanes = False
        Else
            ws.cells.ClearContents
        End If
    Else
        If positionFirst Then
            Set ws = wb.Sheets.Add(before:=wb.Sheets(1))
        Else
            Set ws = wb.Sheets.Add(after:=wb.Sheets(wb.Sheets.Count))
        End If
        ws.name = name
    End If
    
    Set aoEmptyWorksheet = ws
End Function

' The two given sheets will switch places
Function aoSwapSheets(wsA As Worksheet, wsB As Worksheet)
    If wsA.Parent.name = wsB.Parent.name And wsA.name = wsB.name Then
        Exit Function
    End If
    
    Dim wbA As Workbook, wbB As Workbook
    Dim tempWs As Worksheet
    
    Set wbA = wsA.Parent
    Set wbB = wsB.Parent
    
    Set tempWs = wbB.Sheets.Add(after:=wsB)
    wsB.Move after:=wsA
    wsA.Move before:=tempWs
    
    Dim alertsOn As Boolean
    alertsOn = Application.DisplayAlerts
    Application.DisplayAlerts = False
    tempWs.Delete
    Application.DisplayAlerts = alertsOn
End Function

' Sheets in the given workbook will be sorted by ascending
'   alpha-numeric name order
Function aoSortSheets(wb As Workbook)
    Dim i As Long
    Dim SheetNames() As String
    ReDim SheetNames(wb.Sheets.Count - 1)

    For i = 0 To UBound(SheetNames)
        SheetNames(i) = wb.Sheets(i + 1).name
    Next
    SheetNames = aoSort(SheetNames)
    
    For i = 0 To UBound(SheetNames)
        aoSwapSheets wb.Sheets(i + 1), wb.Sheets(SheetNames(i))
    Next
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
'
'
Public Function aoMatch( _
        ByVal rangeA As Variant, ByVal rangeB As Variant, _
        Optional by As Orientation = Orientation.BY_ROW, _
        Optional useStair As Boolean = False) As Collection
    
    Dim aSpans As Collection, bSpans As Collection
    Dim rA As Range, rb As Range
    Dim dictA As Object, dictB As Object
    
    aoClearStairValCache
    
    Set rA = aoExtractRange(rangeA)
    Set rb = aoExtractRange(rangeB)
    
    If Not (rA Is Nothing) Then
        Set rA = Intersect(rA, rA.Parent.UsedRange)
    End If
    
    If Not (rb Is Nothing) Then
        Set rb = Intersect(rb, rb.Parent.UsedRange)
    End If
    
    If (rA Is Nothing) Or (rb Is Nothing) Or _
            (by = Orientation.BY_CELL And useStair) Then
        Set aoMatch = New Collection
        Exit Function
    End If
    
    If by = Orientation.BY_ROW Then
        Set aSpans = aoRows(rA)
        Set bSpans = aoRows(rb)
    ElseIf by = Orientation.BY_COLUMN Then
        Set aSpans = aoCols(rA)
        Set bSpans = aoCols(rb)
    Else
        Set aSpans = aoCellsAsCollection(rA)
        Set bSpans = aoCellsAsCollection(rb)
        
        If useStair Then
            ' cannot use stair with BY_CELL orientation
            Debug.Assert False
        End If
    End If
    
    If useStair Then
        Set dictA = aoDictOfRangeValueVsRange(aSpans, rA, (by = BY_ROW))
        Set dictB = aoDictOfRangeValueVsRange(bSpans, rb, (by = BY_ROW))
    Else
        Set dictA = aoDictOfRangeValueVsRange(aSpans)
        Set dictB = aoDictOfRangeValueVsRange(bSpans)
    End If
    
    Set aoMatch = aoValuesOfMatchingKeys(dictA, dictB)
    aoClearStairValCache
End Function

Private Function aoCellsAsCollection(r As Range) As Collection
    Dim coll As New Collection
    Dim aCell As Range
    
    For Each aCell In r.cells
        coll.Add aCell
    Next
    
    Set aoCellsAsCollection = coll
End Function


Private Function aoValuesOfMatchingKeys( _
        dictA As Object, dictB As Object) As Collection
    
    Dim keyA As Variant, matchingPairs As New Collection
    
    For Each keyA In dictA
        If dictB.exists(keyA) Then
            matchingPairs.Add aoPair(dictA.Item(keyA), dictB.Item(keyA))
        End If
    Next
    
    Set aoValuesOfMatchingKeys = matchingPairs
End Function


Private Function aoDictOfRangeValueVsRange( _
        ranges As Collection, _
        Optional ByVal stairBounds As Variant, _
        Optional isStairVertical As Boolean) As Object
    
    Dim index As Long
    Dim rangeDict As Object
    Dim rangeValue As Variant
    Dim oneCell As Range, oneRange As Range
    
    Set rangeDict = CreateObject("Scripting.Dictionary")
    
    For Each oneRange In ranges
        rangeValue = _
            Trim(aoJoinRangeVals( _
                    oneRange, stairBounds, isStairVertical))
        If IsDate(rangeValue) Then
            rangeValue = Format(CDate(rangeValue), "yyyy-mm-dd")
        End If
        
        Set rangeDict(rangeValue) = _
                aoUnion(rangeDict(rangeValue), oneRange)
    Next
    
    Set aoDictOfRangeValueVsRange = rangeDict
End Function

'
' Given a range returns something that will uniquely
'   identify it by its value.
Function aoJoinRangeVals( _
        ByVal aRange As Variant, _
        Optional ByVal stairBounds As Variant, _
        Optional isStairVertical As Boolean) As String
    
    Dim aCell As Range, useStair As Boolean, stairBoundRange As Range
    Set aRange = aoExtractRange(aRange)
    
    ' grrrr, no short circuiting
    If Not IsMissing(stairBounds) Then
        If Not (stairBounds Is Nothing) Then
            Set stairBoundRange = aoExtractRange(stairBounds)
            
            If Not (stairBoundRange Is Nothing) Then
                useStair = True
            End If
        End If
    End If
    
    aoJoinRangeVals = ""
    For Each aCell In aRange.cells
        If useStair Then
            aoJoinRangeVals = _
                    aoJoinRangeVals & SEP_STR & _
                        aoStairVal( _
                            stairBoundRange, aCell, isStairVertical)
        Else
            aoJoinRangeVals = _
                    aoJoinRangeVals & SEP_STR & aoCellVal(aCell)
        End If
    Next
    aoJoinRangeVals = _
            right( _
                aoJoinRangeVals, _
                Len(aoJoinRangeVals) - Len(SEP_STR))
End Function




''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'
' Spans with the same value inside of their cells are groupped
'

Public Function aoGroup( _
        ByVal groupWhat As Variant, _
        Optional clumpDisjointAreas As Boolean = False, _
        Optional useStair As Boolean = False, _
        Optional by As Orientation = Orientation.BY_ROW) As Collection
    
    Dim spans As Collection, aVal As Variant
    Dim groups As New Collection, bounds As Range
    
    aoClearStairValCache
    Set aoGroup = groups
    
    Set bounds = aoExtractRange(groupWhat)
    If bounds Is Nothing Then
        Exit Function
    Else
        Set bounds = Intersect(bounds, bounds.Parent.UsedRange)
        
        If bounds Is Nothing Then
            Exit Function
        End If
    End If
    
    If by = Orientation.BY_CELL Then
        useStair = False
    End If
    
    Select Case by
        Case Orientation.BY_ROW
            Set spans = aoRows(bounds)
        Case Orientation.BY_COLUMN
            Set spans = aoCols(bounds)
        Case Orientation.BY_CELL
            Dim aCell As Range
            Set spans = New Collection
            
            For Each aCell In bounds.cells
                spans.Add aCell
            Next
            
        Case Else
            Debug.Assert False
    End Select
    
    If by = BY_ROW Then
        Set bounds = bounds.EntireColumn
    Else
        Set bounds = bounds.EntireRow
    End If
    
    If clumpDisjointAreas Then
        Dim dict As Object
        
        If useStair Then
            Set dict = aoDictOfRangeValueVsRange( _
                            spans, bounds, (by = BY_ROW))
        Else
            Set dict = aoDictOfRangeValueVsRange(spans)
        End If
        
        For Each aVal In dict.items
            groups.Add aVal
        Next
    Else
        Dim curVal As String, prevVal As String
        Dim aSpan As Range, aGroup As New AO_RangeBag
         
        prevVal = aoCellValues(spans(1), bounds, useStair, by)
        For Each aSpan In spans
            curVal = aoCellValues(aSpan, bounds, useStair, by)
            
            If prevVal <> curVal Then
                groups.Add aGroup.Flatten
                aGroup.clear
                
                prevVal = curVal
            End If
            
            aGroup.addRange aSpan
        Next
        
        If Not (aGroup.Flatten Is Nothing) Then
            groups.Add aGroup.Flatten
        End If
    End If
    
    aoClearStairValCache
End Function

Private Function aoCellValues( _
        cells As Range, _
        stairBounds As Range, _
        useStair As Boolean, _
        by As Orientation) As String
    
    If useStair Then
        aoCellValues = aoJoinRangeVals(cells, stairBounds, by = BY_ROW)
    Else
        aoCellValues = aoJoinRangeVals(cells)
    End If
End Function


''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' A partition here means a span of non-repeating values.
' This function splits a range in partitions.
'
Function aoPartition( _
        aRange As Variant, _
        Optional byRow As Boolean = True) As Collection
    
    Dim rb As New AO_RangeBag, aSpan As Range, spans As Collection
    Dim lastSeen As String, parts As New Collection
    Dim lookIn As Range
    
    Set aoPartition = parts
    
    Set lookIn = aoExtractRange(aRange)
    If lookIn Is Nothing Then: Exit Function
    Set lookIn = aoIntersect(lookIn, lookIn.Parent)
    If lookIn Is Nothing Then: Exit Function
    
    aoClearStairValCache
    
    If byRow Then
        Set spans = aoRows(lookIn)
    Else
        Set spans = aoCols(lookIn)
    End If
    
    For Each aSpan In spans
        If lastSeen = "" Then
            rb.clear
            rb.addRange aSpan

            lastSeen = aoJoinRangeVals(aSpan)
        Else
            If lastSeen = aoJoinRangeVals(aSpan) Then
                parts.Add rb.Flatten
                
                rb.clear
                rb.addRange aSpan
            Else
                rb.addRange aSpan
            End If
        End If
    Next
    
    If Not rb.Flatten Is Nothing Then
        parts.Add rb.Flatten
    End If
    
    aoClearStairValCache
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Applies formatting to each cell in the range (one cell at a time).
'
Function aoFormat( _
        aRange As Range, _
        Optional borderIndex As Long = -420, _
        Optional borderLineStyle As Long = XlLineStyle.xlContinuous, _
        Optional borderWeight As Long = -420, _
        Optional forceBorder As Boolean = False, _
        Optional interiorColor As Long = -420, _
        Optional forceInterior As Boolean = False)
    Dim aCell As Range, boundedRange As Range
    
    Set boundedRange = aoIntersect(aRange, aRange.Parent.UsedRange)
    For Each aCell In boundedRange.cells
        If Not (borderIndex = -420 Or borderLineStyle = -420) Then
            If forceBorder Or _
                    aCell.Borders(borderIndex).LineStyle = _
                        xlLineStyleNone Then
                    
                With aCell.Borders(borderIndex)
                    .ColorIndex = 1
                    .LineStyle = borderLineStyle
                    
                    If borderWeight <> -420 Then
                        .Weight = borderWeight
                    End If
                End With
            End If
        End If
        
        If interiorColor <> -420 Then
            If forceInterior Or _
                    aCell.Interior.ColorIndex = xlColorIndexNone Then
                aCell.Interior.ColorIndex = interiorColor
            End If
        End If
    Next
End Function

Function aoClearFormat(ws As Worksheet)
    aoTrimWs ws
    
    With ws.cells
        .FormatConditions.Delete
        
        .Font.Bold = False
        .Font.Italic = False
        .Font.ColorIndex = 0
        .Font.size = 8
        .Font.name = "Arial"
        .Interior.ColorIndex = xlColorIndexNone
        .HorizontalAlignment = xlGeneral
        
        .Borders(xlInsideVertical).LineStyle = xlNone
        .Borders(xlInsideHorizontal).LineStyle = xlNone
    End With
    
    ' i have to do this because VBA is an absolutely
    '   terrible languate that sometimes breaks otherwhise
    On Error Resume Next
    ws.rows.Ungroup
    ws.rows.Ungroup
    ws.rows.Ungroup
    'ws.Columns.Ungroup
    'ws.Columns.Ungroup
    'ws.Columns.Ungroup
    
    ws.Activate
    ActiveWindow.FreezePanes = False
End Function



''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' gets around a wierd bug that sometimes prevents pivot table values
'   from being copied over.
Function aoAppendSheet(srcWs As Worksheet, destWs As Worksheet)
    Dim dst As Range, prevSelect As Range
    
    Set prevSelect = Selection
    
    Set dst = aoBL(destWs).EntireRow.cells(1, 1)
    If dst.Row > 1 Then
        Set dst = dst.Offset(1, 0)
    End If
    
    'srcWs.Outline.ShowLevels RowLevels:=10
    
    srcWs.UsedRange.Copy
    dst.PasteSpecial xlPasteValues
    dst.PasteSpecial xlPasteFormats
    
    aoSelect prevSelect
    'aoTrimWs destWs
End Function


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Helps enter formulas for totaling up the values of a bunch of cells.
Public Function aoEnterTotals( _
        ByVal toTotalUp As Range, _
        ByVal enterInto As Range, _
        Optional ByVal by As Orientation = Orientation.AUTOMATIC)
    
    Dim srcSpan As Range
    Dim destSpan As Range, prevCellInSpan As Range, destCell As Range
    Dim backInSpan As Direction, forewardInSpan As Direction
    
    Debug.Assert by <> Orientation.BY_CELL
    
    Set toTotalUp = _
            aoIntersectAsIs(toTotalUp, toTotalUp.Parent.UsedRange)
    Set enterInto = _
            aoIntersectAsIs(enterInto, enterInto.Parent.UsedRange)
    
    If by = Orientation.AUTOMATIC Then
        Debug.Assert _
                toTotalUp.Areas(1).rows.Count = _
                    toTotalUp.EntireRow.rows.Count
        Debug.Assert _
                aoRow(toTotalUp, 1).cells.Count <> _
                    aoCol(toTotalUp, 1).cells.Count
        
        If aoCol(toTotalUp, 1).cells.Count < _
                aoRow(toTotalUp, 1).cells.Count Then
            by = BY_ROW
        Else
            by = BY_COLUMN
        End If
    End If
    
    If by = BY_ROW Then
        backInSpan = LEFT_HAND
        forewardInSpan = RIGHT_HAND
    Else
        backInSpan = UP
        forewardInSpan = Down
    End If
    
    For Each destSpan In aoRowsOrCols(enterInto, 0, by = BY_ROW)
        Set prevCellInSpan = _
                aoRelativeRange(destSpan, backInSpan).cells(1, 1)
        
        For Each destCell In destSpan.cells
            
            Set srcSpan = _
                aoIntersect( _
                    aoRelativeRange(prevCellInSpan, forewardInSpan), _
                    aoRelativeRange(destCell, backInSpan), _
                    toTotalUp)
            
            If Not (srcSpan Is Nothing) Then
                destCell.Formula = _
                        "=SUM(" & Replace(srcSpan.Address, "$", "") & ")"
            End If
            
            Set prevCellInSpan = destCell
        Next
    Next
End Function

'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
' Evaluates an expression into cells.
' Expression syntax:
'   <val:label>
'   <ref:label>
'   <prevGroup:label:initial value>
'   <prev:initial value>
'   <prev:label:initial value>
'   <currentFormula>
'   <closestBackwardCell>
' aoEval(lblColSpan, dataVals, "ab diff", "= <ref:a> - <ref:b>")
' aoEval(lblColSpan, dataVals, "cumulative ab diff", _
'           "= <prev:0> + <ref:ab diff>")
'
' implimentation: odd tokens are literals, even tokens are to be eval'd
Function aoEval( _
        ByVal labels As Range, _
        ByVal evalInto As Range, _
        ByVal label As String, _
        ByVal expression As String, _
        Optional ByVal by As Orientation = Orientation.BY_ROW)
    
    Dim tokens As Collection
    Dim lblGroup As Range
    Dim lblMap As Object, prevLblMap As Object
    Dim destCell As Range, prevDestCell As Range
    Dim destLblCell As Range, destCellsInGroup As Range
    'dim evalStepDomain as Range, prevEvalStepDomain as Range
    
    Debug.Assert by <> Orientation.BY_CELL
    Debug.Assert by <> Orientation.AUTOMATIC
    
    Set labels = aoIntersect(labels, labels.Parent.UsedRange)
    Set evalInto = aoIntersect(evalInto, evalInto.Parent.UsedRange)
    
    
    Set tokens = aoTokenizeOnAngleBrackets(expression)
    For Each lblGroup In aoPartition(labels, by = BY_ROW)
        
        Set lblMap = aoMapTokenLblsToLblCells(lblGroup, tokens)
        Set destLblCell = aoFind(lblGroup, label)
        
        Debug.Assert Not (destLblCell Is Nothing)
        Debug.Assert destLblCell.Count = 1
        
        Set destCellsInGroup = _
                aoIntersect( _
                    aoGetEntireRowsOrCols( _
                        destLblCell, by = BY_ROW), _
                    evalInto)
        
        Set prevDestCell = Nothing
        For Each destCell In destCellsInGroup.cells
            aoEvalTokens _
                lblMap, _
                prevLblMap, _
                Intersect( _
                        aoGetEntireRowsOrCols(lblGroup, by = BY_ROW), _
                        aoGetEntireRowsOrCols(destCell, by <> BY_ROW)), _
                tokens, _
                destCell, _
                prevDestCell, _
                by = BY_ROW
                
            Set prevDestCell = destCell
        Next
        
        Set prevLblMap = lblMap
    Next
End Function

' Called one row at a time, prevDestCell is Nothing
'   at the beginning of each row.
Private Function aoEvalTokens( _
        lblMap As Object, _
        prevLblMap As Object, _
        evalStepDomain As Range, _
        tokens As Collection, _
        destCell As Range, _
        prevDestCell As Range, _
        byRow As Boolean)
    
    Dim out As String, val As String, origFormula As String
    Dim isEvalToken As Boolean
    Dim token As Variant, label As String
    
    origFormula = destCell.Formula
    If InStr(origFormula, "=") = 1 Then
        origFormula = right(origFormula, Len(origFormula) - 1)
    End If
    
    For Each token In tokens
        If isEvalToken Then
            
            If token = "closestBackwardCell" Then
                If byRow Then
                    out = out & destCell.Offset(0, -1).Address
                Else
                    out = out & destCell.Offset(-1, 0).Address
                End If
                
            ElseIf InStr(token, "val:") > 0 Then
                val = _
                    aoCellVal(Intersect( _
                        aoGetEntireRowsOrCols( _
                            lblMap(right(token, Len(token) - 4)), _
                            byRow), _
                        evalStepDomain))
                
                If IsNumeric(val) Then
                    out = out & val
                Else
                    out = out & "0"
                End If
                
            ElseIf InStr(token, "ref:") > 0 Then
                out = out & _
                        Intersect( _
                            aoGetEntireRowsOrCols( _
                                lblMap(right(token, Len(token) - 4)), _
                                byRow), _
                            evalStepDomain).Address
                
            ElseIf InStr(token, "prev:") > 0 Then
                If prevDestCell Is Nothing Then
                    out = out & _
                            right( _
                                token, _
                                Len(token) - InStrRev(token, ":"))
                Else
                    If token Like "prev:*:*" Then
                        label = mid(token, 6, InStrRev(token, ":") - 6)
                        
                        out = out & _
                                Intersect( _
                                    aoGetEntireRowsOrCols( _
                                        lblMap(label), byRow), _
                                    aoGetEntireRowsOrCols( _
                                        prevDestCell, Not byRow)).Address
                    Else
                        out = out & prevDestCell.Address
                    End If
                End If
            
            ElseIf token = "currentFormula" Then
                out = out & origFormula
                
            ElseIf InStr(token, "prevGroup:") > 0 Then
                If prevLblMap Is Nothing Then
                    out = out & _
                            right( _
                                token, _
                                Len(token) - InStrRev(token, ":"))
                Else
                    label = mid( _
                            token, _
                            InStr(token, ":") + 1, _
                            InStrRev(token, ":") - _
                                (InStr(token, ":") + 1))
                    
                    out = out & _
                            Intersect( _
                                aoGetEntireRowsOrCols( _
                                    prevLblMap(label), byRow), _
                                aoGetEntireRowsOrCols( _
                                    evalStepDomain, Not byRow) _
                            ).Address
                End If
                
            Else
                ' unrecognized eval token
                Debug.Assert False
            End If
        Else
            out = out & token
        End If
        
        isEvalToken = Not isEvalToken
    Next
    
    destCell.Formula = Replace(out, "$", "")
End Function

Private Function aoMapTokenLblsToLblCells( _
        labelCells As Range, _
        tokens As Collection) As Object
    
    Dim token As Variant
    Dim lblMap As Object
    Dim isEvalToken As Boolean
    Dim label As String, foundLbls As Range
    
    Set lblMap = CreateObject("Scripting.Dictionary")
    
    For Each token In tokens
        If isEvalToken Then
            If InStr(token, "val:") = 1 Or _
                    InStr(token, "ref:") = 1 Or _
                    token Like "prev:*:*" Or _
                    InStr(token, "prevGroup:") = 1 Then
                
                label = right(token, Len(token) - InStr(token, ":"))
                
                If InStr(label, ":") > 0 Then
                    label = Left(label, InStr(label, ":") - 1)
                End If
                
                If Not (lblMap.exists(label)) Then
                    Set foundLbls = aoFind(labelCells, label)
                    
                    ' specified label not found
                    ' aoSelect labelCells
                    Debug.Assert Not (foundLbls Is Nothing)
                    
                    Set lblMap(label) = foundLbls
                End If
            End If
        End If
        
        isEvalToken = Not isEvalToken
    Next
    
    Set aoMapTokenLblsToLblCells = lblMap
End Function

Private Function aoTokenizeOnAngleBrackets( _
        toParse As String) As Collection
    
    Dim leftBracketToken As Variant
    Dim inTailToken As Boolean
    Dim tokens As New Collection
    
    For Each leftBracketToken In Strings.Split(toParse, "<")
        
        If inTailToken Then
            
            ' missing closing (>) bracket
            Debug.Assert InStr(leftBracketToken, ">") > 0
            
            ' extra closing (>) bracket
            Debug.Assert InStr(leftBracketToken, ">") = _
                            InStrRev(leftBracketToken, ">")
            
            tokens.Add _
                Left( _
                    leftBracketToken, _
                    InStr(leftBracketToken, ">") - 1)
            tokens.Add _
                right( _
                    leftBracketToken, _
                    Len(leftBracketToken) - _
                        InStr(leftBracketToken, ">"))
        Else
            ' closing (>) bracket appears before first
            '   opening (<) bracket. in first (head) token
            Debug.Assert InStr(leftBracketToken, ">") = 0
            
            tokens.Add leftBracketToken
            
            inTailToken = True
        End If
    Next
    
    Set aoTokenizeOnAngleBrackets = tokens
End Function


'---------------------------------------------------------------
Public Function aoCellVal( _
        aCell As Range, _
        Optional ByVal errorVal As String = "err") As String
    
    On Error GoTo handle_error
    
    aoCellVal = aCell.Value
    
    Exit Function
handle_error:
    aoCellVal = errorVal
End Function

Public Function aoCDbl(val As String) As Double
    On Error GoTo handle_error
    
    aoCDbl = CDbl(val)
    
    Exit Function
handle_error:
    aoCDbl = 0
End Function

Public Function aoCDate(val As String) As Date
    If InStr(val, "_") Then
        aoCDate = CDate(right(val, Len(val) - InStr(val, "_")))
    Else
        aoCDate = CDate(val)
    End If
End Function

Public Function aoMakeFormulasRelative( _
        lookIn As Variant)
    Dim aCell As Range
    For Each aCell In aoExtractRange(lookIn).cells
        If InStr(aCell.Formula, "=") = 1 Then
            aCell.Formula = Replace(aCell.Formula, "$", "")
        End If
    Next
End Function


