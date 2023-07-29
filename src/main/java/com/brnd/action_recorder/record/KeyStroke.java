package com.brnd.action_recorder.record;

public class KeyStroke {
    private int index;
    private KeyType keyType;
    private char keyChar;
}

enum KeyType{
    ALPHANUMERICAL
    ,CONTROL
    ,FUNCTIONAL
    ,NAVIGATIONAL
    ,ESPECIAL
}
