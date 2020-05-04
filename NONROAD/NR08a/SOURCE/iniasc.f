C**** INIASC
c
      subroutine iniasc()
c
c-----------------------------------------------------------------------
c
c    Initiliazes the equipment codes.
c
c-----------------------------------------------------------------------
c    LOG:
c-----------------------------------------------------------------------
c
c      06/10/97  --gwilson--  original development
c      04/03/02 charvey  adds detailed GSE SCC's.
c      04/27/05 --cimulus-- changed Hyrdo power units from 22xx005050
c                           to 22xx006035, moving their categories and
c                           renumbering as needed.  The total array size
c                           did not change.
c      03/09/2007 charvey  adds 2260008005 & 2268008005 GSE SCC's. Removed 2285002005.
c                          Removed 2285002005.
c
c-----------------------------------------------------------------------
c    Include files:
c-----------------------------------------------------------------------
c
      IMPLICIT NONE

      include 'nonrdprm.inc'
      include 'nonrdusr.inc'
      include 'nonrdreg.inc'
c
c-----------------------------------------------------------------------
c    Entry point:
c-----------------------------------------------------------------------
c
c   --- equipment category and equipment name arrays ----
c
c --- Recreational Equipment ---
c
       eqpcod(   1) = "2260001010"
       eqpcod(   2) = "2260001020"
       eqpcod(   3) = "2260001030"
       eqpcod(   4) = "2260001040"
       eqpcod(   5) = "2260001050"
       eqpcod(   6) = "2260001060"
       eqpcod(   7) = "2265001010"
       eqpcod(   8) = "2265001020"
       eqpcod(   9) = "2265001030"
       eqpcod(  10) = "2265001040"
       eqpcod(  11) = "2265001050"
       eqpcod(  12) = "2265001060"
       eqpcod(  13) = "2267001010"
       eqpcod(  14) = "2267001020"
       eqpcod(  15) = "2267001030"
       eqpcod(  16) = "2267001040"
       eqpcod(  17) = "2267001050"
       eqpcod(  18) = "2267001060"
       eqpcod(  19) = "2268001010"
       eqpcod(  20) = "2268001020"
       eqpcod(  21) = "2268001030"
       eqpcod(  22) = "2268001040"
       eqpcod(  23) = "2268001050"
       eqpcod(  24) = "2268001060"
       eqpcod(  25) = "2270001010"
       eqpcod(  26) = "2270001020"
       eqpcod(  27) = "2270001030"
       eqpcod(  28) = "2270001040"
       eqpcod(  29) = "2270001050"
       eqpcod(  30) = "2270001060"
c
c --- Construction and Mining Equipment ---
c
       eqpcod(  31) = "2260002003"
       eqpcod(  32) = "2260002006"
       eqpcod(  33) = "2260002009"
       eqpcod(  34) = "2260002015"
       eqpcod(  35) = "2260002018"
       eqpcod(  36) = "2260002021"
       eqpcod(  37) = "2260002024"
       eqpcod(  38) = "2260002027"
       eqpcod(  39) = "2260002030"
       eqpcod(  40) = "2260002033"
       eqpcod(  41) = "2260002036"
       eqpcod(  42) = "2260002039"
       eqpcod(  43) = "2260002042"
       eqpcod(  44) = "2260002045"
       eqpcod(  45) = "2260002048"
       eqpcod(  46) = "2260002051"
       eqpcod(  47) = "2260002054"
       eqpcod(  48) = "2260002057"
       eqpcod(  49) = "2260002060"
       eqpcod(  50) = "2260002063"
       eqpcod(  51) = "2260002066"
       eqpcod(  52) = "2260002069"
       eqpcod(  53) = "2260002072"
       eqpcod(  54) = "2260002075"
       eqpcod(  55) = "2260002078"
       eqpcod(  56) = "2260002081"
       eqpcod(  57) = "2265002003"
       eqpcod(  58) = "2265002006"
       eqpcod(  59) = "2265002009"
       eqpcod(  60) = "2265002015"
       eqpcod(  61) = "2265002018"
       eqpcod(  62) = "2265002021"
       eqpcod(  63) = "2265002024"
       eqpcod(  64) = "2265002027"
       eqpcod(  65) = "2265002030"
       eqpcod(  66) = "2265002033"
       eqpcod(  67) = "2265002036"
       eqpcod(  68) = "2265002039"
       eqpcod(  69) = "2265002042"
       eqpcod(  70) = "2265002045"
       eqpcod(  71) = "2265002048"
       eqpcod(  72) = "2265002051"
       eqpcod(  73) = "2265002054"
       eqpcod(  74) = "2265002057"
       eqpcod(  75) = "2265002060"
       eqpcod(  76) = "2265002063"
       eqpcod(  77) = "2265002066"
       eqpcod(  78) = "2265002069"
       eqpcod(  79) = "2265002072"
       eqpcod(  80) = "2265002075"
       eqpcod(  81) = "2265002078"
       eqpcod(  82) = "2265002081"
       eqpcod(  83) = "2267002003"
       eqpcod(  84) = "2267002006"
       eqpcod(  85) = "2267002009"
       eqpcod(  86) = "2267002015"
       eqpcod(  87) = "2267002018"
       eqpcod(  88) = "2267002021"
       eqpcod(  89) = "2267002024"
       eqpcod(  90) = "2267002027"
       eqpcod(  91) = "2267002030"
       eqpcod(  92) = "2267002033"
       eqpcod(  93) = "2267002036"
       eqpcod(  94) = "2267002039"
       eqpcod(  95) = "2267002042"
       eqpcod(  96) = "2267002045"
       eqpcod(  97) = "2267002048"
       eqpcod(  98) = "2267002051"
       eqpcod(  99) = "2267002054"
       eqpcod( 100) = "2267002057"
       eqpcod( 101) = "2267002060"
       eqpcod( 102) = "2267002063"
       eqpcod( 103) = "2267002066"
       eqpcod( 104) = "2267002069"
       eqpcod( 105) = "2267002072"
       eqpcod( 106) = "2267002075"
       eqpcod( 107) = "2267002078"
       eqpcod( 108) = "2267002081"
       eqpcod( 109) = "2268002003"
       eqpcod( 110) = "2268002006"
       eqpcod( 111) = "2268002009"
       eqpcod( 112) = "2268002015"
       eqpcod( 113) = "2268002018"
       eqpcod( 114) = "2268002021"
       eqpcod( 115) = "2268002024"
       eqpcod( 116) = "2268002027"
       eqpcod( 117) = "2268002030"
       eqpcod( 118) = "2268002033"
       eqpcod( 119) = "2268002036"
       eqpcod( 120) = "2268002039"
       eqpcod( 121) = "2268002042"
       eqpcod( 122) = "2268002045"
       eqpcod( 123) = "2268002048"
       eqpcod( 124) = "2268002051"
       eqpcod( 125) = "2268002054"
       eqpcod( 126) = "2268002057"
       eqpcod( 127) = "2268002060"
       eqpcod( 128) = "2268002063"
       eqpcod( 129) = "2268002066"
       eqpcod( 130) = "2268002069"
       eqpcod( 131) = "2268002072"
       eqpcod( 132) = "2268002075"
       eqpcod( 133) = "2268002078"
       eqpcod( 134) = "2268002081"
       eqpcod( 135) = "2270002003"
       eqpcod( 136) = "2270002006"
       eqpcod( 137) = "2270002009"
       eqpcod( 138) = "2270002015"
       eqpcod( 139) = "2270002018"
       eqpcod( 140) = "2270002021"
       eqpcod( 141) = "2270002024"
       eqpcod( 142) = "2270002027"
       eqpcod( 143) = "2270002030"
       eqpcod( 144) = "2270002033"
       eqpcod( 145) = "2270002036"
       eqpcod( 146) = "2270002039"
       eqpcod( 147) = "2270002042"
       eqpcod( 148) = "2270002045"
       eqpcod( 149) = "2270002048"
       eqpcod( 150) = "2270002051"
       eqpcod( 151) = "2270002054"
       eqpcod( 152) = "2270002057"
       eqpcod( 153) = "2270002060"
       eqpcod( 154) = "2270002063"
       eqpcod( 155) = "2270002066"
       eqpcod( 156) = "2270002069"
       eqpcod( 157) = "2270002072"
       eqpcod( 158) = "2270002075"
       eqpcod( 159) = "2270002078"
       eqpcod( 160) = "2270002081"
c
c --- Industrial Equipment ---
c
       eqpcod( 161) = "2260003010"
       eqpcod( 162) = "2260003020"
       eqpcod( 163) = "2260003030"
       eqpcod( 164) = "2260003040"
       eqpcod( 165) = "2260003050"
       eqpcod( 166) = "2260003060"
       eqpcod( 167) = "2260003070"
       eqpcod( 168) = "2265003010"
       eqpcod( 169) = "2265003020"
       eqpcod( 170) = "2265003030"
       eqpcod( 171) = "2265003040"
       eqpcod( 172) = "2265003050"
       eqpcod( 173) = "2265003060"
       eqpcod( 174) = "2265003070"
       eqpcod( 175) = "2267003010"
       eqpcod( 176) = "2267003020"
       eqpcod( 177) = "2267003030"
       eqpcod( 178) = "2267003040"
       eqpcod( 179) = "2267003050"
       eqpcod( 180) = "2267003060"
       eqpcod( 181) = "2267003070"
       eqpcod( 182) = "2268003010"
       eqpcod( 183) = "2268003020"
       eqpcod( 184) = "2268003030"
       eqpcod( 185) = "2268003040"
       eqpcod( 186) = "2268003050"
       eqpcod( 187) = "2268003060"
       eqpcod( 188) = "2268003070"
       eqpcod( 189) = "2270003010"
       eqpcod( 190) = "2270003020"
       eqpcod( 191) = "2270003030"
       eqpcod( 192) = "2270003040"
       eqpcod( 193) = "2270003050"
       eqpcod( 194) = "2270003060"
       eqpcod( 195) = "2270003070"
c
c --- Lawn and Garden Equipment ---
c
       eqpcod( 196) = "2260004010"
       eqpcod( 197) = "2260004011"
       eqpcod( 198) = "2260004015"
       eqpcod( 199) = "2260004016"
       eqpcod( 200) = "2260004020"
       eqpcod( 201) = "2260004021"
       eqpcod( 202) = "2260004025"
       eqpcod( 203) = "2260004026"
       eqpcod( 204) = "2260004030"
       eqpcod( 205) = "2260004031"
       eqpcod( 206) = "2260004035"
       eqpcod( 207) = "2260004036"
       eqpcod( 208) = "2260004040"
       eqpcod( 209) = "2260004041"
       eqpcod( 210) = "2260004045"
       eqpcod( 211) = "2260004046"
       eqpcod( 212) = "2260004050"
       eqpcod( 213) = "2260004051"
       eqpcod( 214) = "2260004055"
       eqpcod( 215) = "2260004056"
       eqpcod( 216) = "2260004060"
       eqpcod( 217) = "2260004061"
       eqpcod( 218) = "2260004065"
       eqpcod( 219) = "2260004066"
       eqpcod( 220) = "2260004071"
       eqpcod( 221) = "2260004075"
       eqpcod( 222) = "2260004076"
       eqpcod( 223) = "2265004010"
       eqpcod( 224) = "2265004011"
       eqpcod( 225) = "2265004015"
       eqpcod( 226) = "2265004016"
       eqpcod( 227) = "2265004020"
       eqpcod( 228) = "2265004021"
       eqpcod( 229) = "2265004025"
       eqpcod( 230) = "2265004026"
       eqpcod( 231) = "2265004030"
       eqpcod( 232) = "2265004031"
       eqpcod( 233) = "2265004035"
       eqpcod( 234) = "2265004036"
       eqpcod( 235) = "2265004040"
       eqpcod( 236) = "2265004041"
       eqpcod( 237) = "2265004045"
       eqpcod( 238) = "2265004046"
       eqpcod( 239) = "2265004050"
       eqpcod( 240) = "2265004051"
       eqpcod( 241) = "2265004055"
       eqpcod( 242) = "2265004056"
       eqpcod( 243) = "2265004060"
       eqpcod( 244) = "2265004061"
       eqpcod( 245) = "2265004065"
       eqpcod( 246) = "2265004066"
       eqpcod( 247) = "2265004071"
       eqpcod( 248) = "2265004075"
       eqpcod( 249) = "2265004076"
       eqpcod( 250) = "2267004010"
       eqpcod( 251) = "2267004011"
       eqpcod( 252) = "2267004015"
       eqpcod( 253) = "2267004016"
       eqpcod( 254) = "2267004020"
       eqpcod( 255) = "2267004021"
       eqpcod( 256) = "2267004025"
       eqpcod( 257) = "2267004026"
       eqpcod( 258) = "2267004030"
       eqpcod( 259) = "2267004031"
       eqpcod( 260) = "2267004035"
       eqpcod( 261) = "2267004036"
       eqpcod( 262) = "2267004040"
       eqpcod( 263) = "2267004041"
       eqpcod( 264) = "2267004045"
       eqpcod( 265) = "2267004046"
       eqpcod( 266) = "2267004050"
       eqpcod( 267) = "2267004051"
       eqpcod( 268) = "2267004055"
       eqpcod( 269) = "2267004056"
       eqpcod( 270) = "2267004060"
       eqpcod( 271) = "2267004061"
       eqpcod( 272) = "2267004065"
       eqpcod( 273) = "2267004066"
       eqpcod( 274) = "2267004071"
       eqpcod( 275) = "2267004075"
       eqpcod( 276) = "2267004076"
       eqpcod( 277) = "2268004010"
       eqpcod( 278) = "2268004011"
       eqpcod( 279) = "2268004015"
       eqpcod( 280) = "2268004016"
       eqpcod( 281) = "2268004020"
       eqpcod( 282) = "2268004021"
       eqpcod( 283) = "2268004025"
       eqpcod( 284) = "2268004026"
       eqpcod( 285) = "2268004030"
       eqpcod( 286) = "2268004031"
       eqpcod( 287) = "2268004035"
       eqpcod( 288) = "2268004036"
       eqpcod( 289) = "2268004040"
       eqpcod( 290) = "2268004041"
       eqpcod( 291) = "2268004045"
       eqpcod( 292) = "2268004046"
       eqpcod( 293) = "2268004050"
       eqpcod( 294) = "2268004051"
       eqpcod( 295) = "2268004055"
       eqpcod( 296) = "2268004056"
       eqpcod( 297) = "2268004060"
       eqpcod( 298) = "2268004061"
       eqpcod( 299) = "2268004065"
       eqpcod( 300) = "2268004066"
       eqpcod( 301) = "2268004071"
       eqpcod( 302) = "2268004075"
       eqpcod( 303) = "2268004076"
       eqpcod( 304) = "2270004010"
       eqpcod( 305) = "2270004011"
       eqpcod( 306) = "2270004015"
       eqpcod( 307) = "2270004016"
       eqpcod( 308) = "2270004020"
       eqpcod( 309) = "2270004021"
       eqpcod( 310) = "2270004025"
       eqpcod( 311) = "2270004026"
       eqpcod( 312) = "2270004030"
       eqpcod( 313) = "2270004031"
       eqpcod( 314) = "2270004035"
       eqpcod( 315) = "2270004036"
       eqpcod( 316) = "2270004040"
       eqpcod( 317) = "2270004041"
       eqpcod( 318) = "2270004045"
       eqpcod( 319) = "2270004046"
       eqpcod( 320) = "2270004050"
       eqpcod( 321) = "2270004051"
       eqpcod( 322) = "2270004055"
       eqpcod( 323) = "2270004056"
       eqpcod( 324) = "2270004060"
       eqpcod( 325) = "2270004061"
       eqpcod( 326) = "2270004065"
       eqpcod( 327) = "2270004066"
       eqpcod( 328) = "2270004071"
       eqpcod( 329) = "2270004075"
       eqpcod( 330) = "2270004076"
c
c --- Agricultural Equipment ---
c
       eqpcod( 331) = "2260005010"
       eqpcod( 332) = "2260005015"
       eqpcod( 333) = "2260005020"
       eqpcod( 334) = "2260005025"
       eqpcod( 335) = "2260005030"
       eqpcod( 336) = "2260005035"
       eqpcod( 337) = "2260005040"
       eqpcod( 338) = "2260005045"
       eqpcod( 339) = "2260005055"
       eqpcod( 340) = "2260005060"
       eqpcod( 341) = "2265005010"
       eqpcod( 342) = "2265005015"
       eqpcod( 343) = "2265005020"
       eqpcod( 344) = "2265005025"
       eqpcod( 345) = "2265005030"
       eqpcod( 346) = "2265005035"
       eqpcod( 347) = "2265005040"
       eqpcod( 348) = "2265005045"
       eqpcod( 349) = "2265005055"
       eqpcod( 350) = "2265005060"
       eqpcod( 351) = "2267005010"
       eqpcod( 352) = "2267005015"
       eqpcod( 353) = "2267005020"
       eqpcod( 354) = "2267005025"
       eqpcod( 355) = "2267005030"
       eqpcod( 356) = "2267005035"
       eqpcod( 357) = "2267005040"
       eqpcod( 358) = "2267005045"
       eqpcod( 359) = "2267005055"
       eqpcod( 360) = "2267005060"
       eqpcod( 361) = "2268005010"
       eqpcod( 362) = "2268005015"
       eqpcod( 363) = "2268005020"
       eqpcod( 364) = "2268005025"
       eqpcod( 365) = "2268005030"
       eqpcod( 366) = "2268005035"
       eqpcod( 367) = "2268005040"
       eqpcod( 368) = "2268005045"
       eqpcod( 369) = "2268005055"
       eqpcod( 370) = "2268005060"
       eqpcod( 371) = "2270005010"
       eqpcod( 372) = "2270005015"
       eqpcod( 373) = "2270005020"
       eqpcod( 374) = "2270005025"
       eqpcod( 375) = "2270005030"
       eqpcod( 376) = "2270005035"
       eqpcod( 377) = "2270005040"
       eqpcod( 378) = "2270005045"
       eqpcod( 379) = "2270005055"
       eqpcod( 380) = "2270005060"
c
c --- Commercial Equipment ---
c
       eqpcod( 381) = "2260006005"
       eqpcod( 382) = "2260006010"
       eqpcod( 383) = "2260006015"
       eqpcod( 384) = "2260006020"
       eqpcod( 385) = "2260006025"
       eqpcod( 386) = "2260006030"
       eqpcod( 387) = "2260006035"
       eqpcod( 388) = "2265006005"
       eqpcod( 389) = "2265006010"
       eqpcod( 390) = "2265006015"
       eqpcod( 391) = "2265006020"
       eqpcod( 392) = "2265006025"
       eqpcod( 393) = "2265006030"
       eqpcod( 394) = "2265006035"
       eqpcod( 395) = "2267006005"
       eqpcod( 396) = "2267006010"
       eqpcod( 397) = "2267006015"
       eqpcod( 398) = "2267006020"
       eqpcod( 399) = "2267006025"
       eqpcod( 400) = "2267006030"
       eqpcod( 401) = "2267006035"
       eqpcod( 402) = "2268006005"
       eqpcod( 403) = "2268006010"
       eqpcod( 404) = "2268006015"
       eqpcod( 405) = "2268006020"
       eqpcod( 406) = "2268006025"
       eqpcod( 407) = "2268006030"
       eqpcod( 408) = "2268006035"
       eqpcod( 409) = "2270006005"
       eqpcod( 410) = "2270006010"
       eqpcod( 411) = "2270006015"
       eqpcod( 412) = "2270006020"
       eqpcod( 413) = "2270006025"
       eqpcod( 414) = "2270006030"
       eqpcod( 415) = "2270006035"
c
c --- Logging Equipment ---
c
       eqpcod( 416) = "2260007005"
       eqpcod( 417) = "2260007010"
       eqpcod( 418) = "2260007015"
       eqpcod( 419) = "2265007005"
       eqpcod( 420) = "2265007010"
       eqpcod( 421) = "2265007015"
       eqpcod( 422) = "2267007005"
       eqpcod( 423) = "2267007010"
       eqpcod( 424) = "2267007015"
       eqpcod( 425) = "2268007005"
       eqpcod( 426) = "2268007010"
       eqpcod( 427) = "2268007015"
       eqpcod( 428) = "2270007005"
       eqpcod( 429) = "2270007010"
       eqpcod( 430) = "2270007015"
c
c --- Airport Ground Support Equipment ---
c
       eqpcod( 431) = "2260008005"
c
       eqpcod( 432) = "2265008005"
       eqpcod( 433) = "2265008010"
       eqpcod( 434) = "2265008015"
       eqpcod( 435) = "2265008020"
       eqpcod( 436) = "2265008025"
       eqpcod( 437) = "2265008030"
       eqpcod( 438) = "2265008035"
       eqpcod( 439) = "2265008040"
       eqpcod( 440) = "2265008045"
       eqpcod( 441) = "2265008050"
       eqpcod( 442) = "2265008055"
       eqpcod( 443) = "2265008060"
       eqpcod( 444) = "2265008065"
       eqpcod( 445) = "2265008070"
       eqpcod( 446) = "2265008075"
       eqpcod( 447) = "2265008080"
       eqpcod( 448) = "2265008085"
       eqpcod( 449) = "2265008090"
       eqpcod( 450) = "2265008095"
       eqpcod( 451) = "2265008100"
       eqpcod( 452) = "2265008101"
       eqpcod( 453) = "2265008102"
       eqpcod( 454) = "2265008103"
       eqpcod( 455) = "2265008104"
       eqpcod( 456) = "2265008105"
       eqpcod( 457) = "2265008106"
       eqpcod( 458) = "2265008107"
       eqpcod( 459) = "2265008108"
       eqpcod( 460) = "2265008109"
       eqpcod( 461) = "2265008110"
       eqpcod( 462) = "2265008111"
c
       eqpcod( 463) = "2267008005"
       eqpcod( 464) = "2267008010"
       eqpcod( 465) = "2267008015"
       eqpcod( 466) = "2267008020"
       eqpcod( 467) = "2267008025"
       eqpcod( 468) = "2267008030"
       eqpcod( 469) = "2267008035"
       eqpcod( 470) = "2267008040"
       eqpcod( 471) = "2267008045"
       eqpcod( 472) = "2267008050"
       eqpcod( 473) = "2267008055"
       eqpcod( 474) = "2267008060"
       eqpcod( 475) = "2267008065"
       eqpcod( 476) = "2267008070"
       eqpcod( 477) = "2267008075"
       eqpcod( 478) = "2267008080"
       eqpcod( 479) = "2267008085"
       eqpcod( 480) = "2267008090"
       eqpcod( 481) = "2267008095"
       eqpcod( 482) = "2267008100"
       eqpcod( 483) = "2267008101"
       eqpcod( 484) = "2267008102"
       eqpcod( 485) = "2267008103"
       eqpcod( 486) = "2267008104"
       eqpcod( 487) = "2267008105"
       eqpcod( 488) = "2267008106"
       eqpcod( 489) = "2267008107"
       eqpcod( 490) = "2267008108"
       eqpcod( 491) = "2267008109"
       eqpcod( 492) = "2267008110"
       eqpcod( 493) = "2267008111"
c
       eqpcod( 494) = "2268008005"
c
       eqpcod( 495) = "2270008005"
       eqpcod( 496) = "2270008010"
       eqpcod( 497) = "2270008015"
       eqpcod( 498) = "2270008020"
       eqpcod( 499) = "2270008025"
       eqpcod( 500) = "2270008030"
       eqpcod( 501) = "2270008035"
       eqpcod( 502) = "2270008040"
       eqpcod( 503) = "2270008045"
       eqpcod( 504) = "2270008050"
       eqpcod( 505) = "2270008055"
       eqpcod( 506) = "2270008060"
       eqpcod( 507) = "2270008065"
       eqpcod( 508) = "2270008070"
       eqpcod( 509) = "2270008075"
       eqpcod( 510) = "2270008080"
       eqpcod( 511) = "2270008085"
       eqpcod( 512) = "2270008090"
       eqpcod( 513) = "2270008095"
       eqpcod( 514) = "2270008100"
       eqpcod( 515) = "2270008101"
       eqpcod( 516) = "2270008102"
       eqpcod( 517) = "2270008103"
       eqpcod( 518) = "2270008104"
       eqpcod( 519) = "2270008105"
       eqpcod( 520) = "2270008106"
       eqpcod( 521) = "2270008107"
       eqpcod( 522) = "2270008108"
       eqpcod( 523) = "2270008109"
       eqpcod( 524) = "2270008110"
       eqpcod( 525) = "2270008111"
c
c --- Other Underground Mining Equipment ---
c
       eqpcod( 526) = "2260009010"
       eqpcod( 527) = "2265009010"
       eqpcod( 528) = "2267009010"
       eqpcod( 529) = "2268009010"
       eqpcod( 530) = "2270009010"
c
c --- Other Oil Field Equipment ---
c
       eqpcod( 531) = "2260010010"
       eqpcod( 532) = "2265010010"
       eqpcod( 533) = "2267010010"
       eqpcod( 534) = "2268010010"
       eqpcod( 535) = "2270010010"
c
c --- Marine Vessels ---
c
       eqpcod( 536) = "2280001010"
       eqpcod( 537) = "2280001020"
       eqpcod( 538) = "2280001030"
       eqpcod( 539) = "2280001040"
       eqpcod( 540) = "2280002010"
       eqpcod( 541) = "2280002020"
       eqpcod( 542) = "2280002030"
       eqpcod( 543) = "2280002040"
       eqpcod( 544) = "2280003010"
       eqpcod( 545) = "2280003020"
       eqpcod( 546) = "2280003030"
       eqpcod( 547) = "2280003040"
       eqpcod( 548) = "2280004010"
       eqpcod( 549) = "2280004020"
       eqpcod( 550) = "2280004030"
       eqpcod( 551) = "2280004040"
c
c --- Pleasure Craft ---
c
       eqpcod( 552) = "2282005010"
       eqpcod( 553) = "2282005015"
       eqpcod( 554) = "2282010005"
       eqpcod( 555) = "2282020005"
       eqpcod( 556) = "2282020010"
       eqpcod( 557) = "2282020025"
c
c --- Railroad Locomotives ---
c
       eqpcod( 558) = "2285002015"
       eqpcod( 559) = "2285003015"
       eqpcod( 560) = "2285004015"
       eqpcod( 561) = "2285006015"
       eqpcod( 562) = "2285008015"
c
c  --- return to calling return ---
c
       goto 9999
c
c-----------------------------------------------------------------------
c  Return point:
c-----------------------------------------------------------------------
c
 9999 continue
      return
      end
