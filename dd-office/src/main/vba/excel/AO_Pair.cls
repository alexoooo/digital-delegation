VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "AO_Pair"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''


Option Explicit

Private valA As Variant
Private valB As Variant


'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Sub init(ByVal aVal As Variant, ByVal bVal As Variant)
    If IsObject(aVal) Then
        Set valA = aVal
    Else
        valA = aVal
    End If
    
    If IsObject(bVal) Then
        Set valB = bVal
    Else
        valB = bVal
    End If
End Sub





'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Property Get a() As Variant
    If IsObject(valA) Then
        Set a = valA
    Else
        a = valA
    End If
End Property

Public Property Let a(ByVal aVal As Variant)
    valA = aVal
End Property

Public Property Set a(ByVal aVal As Variant)
    Set valA = aVal
End Property



'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Public Property Get b() As Variant
    If IsObject(valB) Then
        Set b = valB
    Else
        b = valB
    End If
End Property

Public Property Let b(ByVal bVal As Variant)
    valB = bVal
End Property

Public Property Set b(ByVal bVal As Variant)
    Set valB = bVal
End Property




