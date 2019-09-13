VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "AO_RangeBag"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Option Explicit

Const MAX_BUFFER_INDEX As Long = 16

Private r_ As Range
Private buffer(MAX_BUFFER_INDEX) As Range
Private size As Long
Private conjunctive As Boolean
Private wasIntersected As Boolean


Private Sub Class_Initialize()
    conjunctive = True
End Sub

Public Function setConjunctive(isConjunctive As Boolean)
    If conjunctive <> isConjunctive Then
        Me.clear
        conjunctive = isConjunctive
    End If
End Function

Public Function clear()
    Set r_ = Nothing
    size = 0
    wasIntersected = False
End Function

Public Function addRange(r As Range)
    
    If r Is Nothing Then
        If Not conjunctive Then
            wasIntersected = True
            Set r_ = Nothing
        End If
        
        Exit Function
    End If
    
    If size > MAX_BUFFER_INDEX Then
        mergeBuffer
    End If
    
    Set buffer(size) = r
    size = size + 1
    
End Function

Public Function Add(ByRef r As Variant)
    Dim asRange As Range
    Set asRange = aoExtractRange(r)
    
    'If Not (asRange Is Nothing) Then
        addRange asRange
    'End If
End Function

Private Function mergeBuffer()
    If conjunctive Then
        mergeBufferConjunctive
    Else
        mergeBufferDisjunctive
    End If
End Function

Private Function mergeBufferConjunctive()
    Dim i As Long, lo As Long
    Dim toMerge As Range

    Debug.Assert MAX_BUFFER_INDEX <= 29
    
    Select Case size
        Case 0:
            Exit Function
        
        Case 1:
            Set toMerge = buffer(0)
        
        Case 2:
            Set toMerge = Union(buffer(0), buffer(1))
        
        Case 3:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2))
        
        Case 4:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3))
        
        Case 5:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4))
            
        Case 6:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5))
        Case 7:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6))
        Case 8:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7))
            
        Case 9:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8))
        
        Case 10:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9))
        Case 11:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10))
        Case 12:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11))
        Case 13:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12))
        Case 14:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13))
        Case 15:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14))
                                
        Case 16:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14), buffer(15))
                                
        Case 17:
            Set toMerge = _
                    Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14), buffer(15), buffer(16))
        Case 18:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17))
                                
        Case 19:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18))
        Case 20:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19))
        Case 21:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20))
        Case 22:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21))
                                
        Case 23:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22))
                                
        Case 24:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23))
                                
        Case 25:
            Set toMerge = Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24))
                                
        Case 26:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25))
                                
        Case 27:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26))
                                
        Case 28:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27))
                                
        Case 29:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27), buffer(28))
                                
        Case 30:
            Set toMerge = _
                      Union(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27), buffer(28), buffer(29))
    End Select
    
    If r_ Is Nothing Then
        Set r_ = toMerge
    Else
        Set r_ = Union(r_, toMerge)
    End If
    
    size = 0
End Function

Private Function mergeBufferDisjunctive()
    Dim i As Long, lo As Long
    Dim toCross As Range

    Debug.Assert MAX_BUFFER_INDEX <= 29
    
    If wasIntersected And r_ Is Nothing Then
        Exit Function
    End If
    
    Select Case size
        Case 0:
            Exit Function
        
        Case 1:
            Set toCross = buffer(0)
        
        Case 2:
            Set toCross = Intersect(buffer(0), buffer(1))
        
        Case 3:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2))
        
        Case 4:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3))
        
        Case 5:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4))
            
        Case 6:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5))
        
        Case 7:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6))
            
        Case 8:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7))
            
        Case 9:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8))
        
        Case 10:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9))
        
        Case 11:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10))

        Case 12:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11))

        Case 13:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12))

        Case 14:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13))
        
        Case 15:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14))
                                
        Case 16:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14), buffer(15))
                                
        Case 17:
            Set toCross = _
                    Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                        buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                        buffer(10), buffer(11), buffer(12), buffer(13), _
                        buffer(14), buffer(15), buffer(16))
                                
        Case 18:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17))
                                
        Case 19:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18))

        Case 20:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19))
                                
        Case 21:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20))

        Case 22:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21))
                                
        Case 23:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22))
                                
        Case 24:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23))
                                
        Case 25:
            Set toCross = Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24))
                                
        Case 26:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25))
                                
        Case 27:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26))
                                
        Case 28:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27))
                                
        Case 29:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27), buffer(28))
                                
        Case 30:
            Set toCross = _
                      Intersect(buffer(0), buffer(1), buffer(2), buffer(3), buffer(4), _
                                buffer(5), buffer(6), buffer(7), buffer(8), buffer(9), _
                                buffer(10), buffer(11), buffer(12), buffer(13), _
                                buffer(14), buffer(15), buffer(16), buffer(17), _
                                buffer(18), buffer(19), buffer(20), buffer(21), _
                                buffer(22), buffer(23), buffer(24), buffer(25), _
                                buffer(26), buffer(27), buffer(28), buffer(29))
    End Select
    
    If r_ Is Nothing Then
        Set r_ = toCross
    Else
        Set r_ = Intersect(r_, toCross)
    End If
    
    size = 0
    wasIntersected = True
End Function


' Return the range represented by this bag
Public Function Flatten() As Range
    mergeBuffer
    Set Flatten = r_
End Function

' Flattens in the process
Public Function sort() As Range
    mergeBuffer
    Set r_ = aoSortAreas(r_)
    Set sort = r_
End Function


