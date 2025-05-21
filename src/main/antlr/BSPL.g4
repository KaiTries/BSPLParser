grammar BSPL;

// Comments
LINE_COMMENT: '#' ~[\r\n]* -> channel(HIDDEN);
LINE_COMMENT_CPP: '//' ~[\r\n]* -> channel(HIDDEN);

// Whitespace
WS: [ \t\r\n]+ -> channel(HIDDEN);

// Top-level rule
document: (protocol)+ EOF;

// Protocol definition
protocol: ('protocol')? name=WORD '{'
            'roles' roles
            'parameters' params
            ('private' params)?
            references
          '}';

// Roles
roles: role (',' role)*;
role: name=WORD;

// Parameters
params: param (',' param)*;
param: (adornment)? name=WORD ('key')?;

// Adornment
adornment: 'out' | 'in' | 'nil' | 'any' | 'opt';

// References
references: (message | ref)*;

// Reference to another protocol
ref: name=spacename '(' (rolesList=roles '|')? paramsList=params ')';

// Message
message: sender=WORD ('->' | '→' | '↦') recipient=WORD (':' name=WORD)? ('[' parameters=params ']' |);

// Tokens
WORD: [a-zA-Z0-9_@>-]+;
spacename: WORD (WS WORD)*;