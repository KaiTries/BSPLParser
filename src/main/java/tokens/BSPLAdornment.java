package tokens;

/*
 * Adornment can be any of  'out' | 'in' | 'nil' | 'any' | 'opt' ;
 */
public enum BSPLAdornment {
  OUT("out"),
  IN("in"),
  NIL("nil"),
  ANY("any"),
  OPT("opt");

  private final String value;

  BSPLAdornment(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static BSPLAdornment fromString(String value) {
    for (BSPLAdornment adornment : BSPLAdornment.values()) {
      if (adornment.getValue().equalsIgnoreCase(value)) {
        return adornment;
      }
    }
    throw new IllegalArgumentException("No constant with value " + value + " found");
  }

  public static boolean isValid(String value) {
    for (BSPLAdornment adornment : BSPLAdornment.values()) {
      if (adornment.getValue().equalsIgnoreCase(value)) {
        return true;
      }
    }
    return false;
  }
}
