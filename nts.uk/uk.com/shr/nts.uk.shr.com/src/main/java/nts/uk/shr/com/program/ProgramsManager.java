/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.shr.com.program;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ProgramsManager
 */
public class ProgramsManager {

    /**
     * QMM011
     */
    public static final Program QMM011A = new Program(WebAppId.PR, ProgramIdConsts.QMM011A, "QMM011_60",
            "/view/qmm/011/a/index.xhtml");
    public static final Program QMM011B = new Program(WebAppId.PR, ProgramIdConsts.QMM011B, "QMM011_61",
            "/view/qmm/011/b/index.xhtml");
    public static final Program Q5MM011C = new Program(WebAppId.PR, ProgramIdConsts.QMM011C, "QMM011_62",
            "/view/qmm/011/c/index.xhtml");
    public static final Program QMM011D = new Program(WebAppId.PR, ProgramIdConsts.QMM011D, "QMM011_63",
            "/view/qmm/011/d/index.xhtml");
    public static final Program QMM011E = new Program(WebAppId.PR, ProgramIdConsts.QMM011E, "QMM011_64",
            "/view/qmm/011/e/index.xhtml");
    public static final Program QMM011F = new Program(WebAppId.PR, ProgramIdConsts.QMM011F, "QMM011_65",
            "/view/qmm/011/f/index.xhtml");

    /**
     * QMM037
     */
    public static final Program QMM037A = new Program(WebAppId.PR, ProgramIdConsts.QMM037A, "QMM037_1",
            "/view/qmm/037/a/index.xhtml");
    /**
     * QMM007
     */
    public static final Program QMM007A = new Program(WebAppId.PR, ProgramIdConsts.QMM007A, "QMM007_46",
            "/view/qmm/007/a/index.xhtml");
    public static final Program QMM007B = new Program(WebAppId.PR, ProgramIdConsts.QMM007B, "QMM007_46",
            "/view/qmm/007/b/index.xhtml");
    public static final Program QMM007C = new Program(WebAppId.PR, ProgramIdConsts.QMM007C, "QMM007_47",
            "/view/qmm/007/c/index.xhtml");
    /**
     * QMM001
     */
    public static final Program QMM001A = new Program(WebAppId.PR, ProgramIdConsts.QMM001A, "QMM001_1",
            "/view/qmm/001/a/index.xhtml");
    public static final Program QMM001B = new Program(WebAppId.PR, ProgramIdConsts.QMM001B, "QMM001_2",
            "/view/qmm/001/b/index.xhtml");
    public static final Program QMM001C = new Program(WebAppId.PR, ProgramIdConsts.QMM001C, "QMM001_3",
            "/view/qmm/001/c/index.xhtml");

    /**
     * CCG005B
     */
    public static final Program CCG005b = new Program(WebAppId.COM, ProgramIdConsts.CCG005b, "CCG005_28",
            "/view/ccg/005/b/index.xhtml");
    
    /**
     * CCG005D
     */
    public static final Program CCG005d = new Program(WebAppId.COM, ProgramIdConsts.CCG005d, "CCG005_29",
            "/view/ccg/005/d/index.xhtml");
    
    /**
     * CCG005E
     */
    public static final Program CCG005E = new Program(WebAppId.COM, ProgramIdConsts.CCG005E, "CCG005_30",
            "/view/ccg/005/e/index.xhtml");
    
    /**
     * CCG008A
     */
    public static final Program CCG008A = new Program(WebAppId.COM, ProgramIdConsts.CCG008A, "CCG008_1",
            "/view/ccg/008/a/index.xhtml");
    /**
     * CCG008B
     */
    public static final Program CCG008B = new Program(WebAppId.COM, ProgramIdConsts.CCG008B, "CCG008_2",
            "/view/ccg/008/b/index.xhtml");
    /**
     * CCG008C
     */
    public static final Program CCG008C = new Program(WebAppId.COM, ProgramIdConsts.CCG008C, "CCG008_3",
            "/view/ccg/008/c/index.xhtml");
    /**
     * CCG008D
     */
    public static final Program CCG008D = new Program(WebAppId.COM, ProgramIdConsts.CCG008D, "",
            "/view/ccg/008/d/index.xhtml");
    
    /**
     * CCG008E
     */
    public static final Program CCG008E = new Program(WebAppId.COM, ProgramIdConsts.CCG008E, "CCG008_26",
            "/view/ccg/008/e/index.xhtml");
    /**
     * CCG013A
     */
    public static final Program CCG013A = new Program(WebAppId.COM, ProgramIdConsts.CCG013A, "CCG013_112",
            "/view/ccg/013/a/index.xhtml");
    /**
     * CCG013B
     */
    public static final Program CCG013B = new Program(WebAppId.COM, ProgramIdConsts.CCG013B, "CCG013_113",
            "/view/ccg/013/b/index.xhtml");
    /**
     * CCG013C
     */
    public static final Program CCG013C = new Program(WebAppId.COM, ProgramIdConsts.CCG013C, "CCG013_114",
            "/view/ccg/013/c/index.xhtml");
    /**
     * CCG013D
     */
    public static final Program CCG013D = new Program(WebAppId.COM, ProgramIdConsts.CCG013D, "CCG013_115",
            "/view/ccg/013/d/index.xhtml");
    /**
     * CCG013E
     */
    public static final Program CCG013E = new Program(WebAppId.COM, ProgramIdConsts.CCG013E, "CCG013_116",
            "/view/ccg/013/e/index.xhtml");
    /**
     * CCG013F
     */
    public static final Program CCG013F = new Program(WebAppId.COM, ProgramIdConsts.CCG013F, "CCG013_117",
            "/view/ccg/013/f/index.xhtml");
    /**
     * CCG013G
     */
    public static final Program CCG013G = new Program(WebAppId.COM, ProgramIdConsts.CCG013G, "CCG013_118",
            "/view/ccg/013/g/index.xhtml");
    /**
     * CCG013I
     */
    public static final Program CCG013I = new Program(WebAppId.COM, ProgramIdConsts.CCG013I, "CCG013_119",
            "/view/ccg/013/i/index.xhtml");
    /**
     * CCG013J
     */
    public static final Program CCG013J = new Program(WebAppId.COM, ProgramIdConsts.CCG013J, "CCG013_120",
            "/view/ccg/013/j/index.xhtml");
    /**
     * CCG013K
     */
    public static final Program CCG013K = new Program(WebAppId.COM, ProgramIdConsts.CCG013K, "CCG013_121",
            "/view/ccg/013/k/index.xhtml");
    /**
     * CCG015A
     */
    public static final Program CCG015A = new Program(WebAppId.COM, ProgramIdConsts.CCG015A, "CCG015_45",
            "/view/ccg/015/a/index.xhtml");

    /**
     * CCG015B
     */
    public static final Program CCG015B = new Program(WebAppId.COM, ProgramIdConsts.CCG015B, "CCG015_1",
            "/view/ccg/015/b/index.xhtml");
    /**
     * CCG015C
     */
    public static final Program CCG015C = new Program(WebAppId.COM, ProgramIdConsts.CCG015C, "CCG015_3",
            "/view/ccg/015/c/index.xhtml");
    /**
     * CCG015D
     */
    public static final Program CCG015D = new Program(WebAppId.COM, ProgramIdConsts.CCG015D, "CCG015_24",
            "/view/ccg/015/d/index.xhtml");
    /**
     * CCG015E
     */
    public static final Program CCG015E = new Program(WebAppId.COM, ProgramIdConsts.CCG015E, "CCG015_25",
            "/view/ccg/015/e/index.xhtml");
    /**
     * CCG015F
     */
    public static final Program CCG015F = new Program(WebAppId.COM, ProgramIdConsts.CCG015F, "CCG015_63",
            "/view/ccg/015/f/index.xhtml");
    
    /**
     * CCG015G
     */
    public static final Program CCG015G = new Program(WebAppId.COM, ProgramIdConsts.CCG015G, "CCG015_89",
            "/view/ccg/015/g/index.xhtml");
    
    /**
     * CCG015H
     */
    public static final Program CCG015H = new Program(WebAppId.COM, ProgramIdConsts.CCG015H, "CCG015_90",
            "/view/ccg/015/h/index.xhtml");
    
    /**
     * CCG014A
     */
    public static final Program CCG014A = new Program(WebAppId.COM, ProgramIdConsts.CCG014A, "CCG014_1",
            "/view/ccg/014/a/index.xhtml");

    /**
     * CCG014B
     */
    public static final Program CCG014B = new Program(WebAppId.COM, ProgramIdConsts.CCG014B, "CCG014_16",
            "/view/ccg/014/b/index.xhtml");
    /**
     * CCG018A
     */
    public static final Program CCG018A = new Program(WebAppId.COM, ProgramIdConsts.CCG018A, "CCG018_39",
            "/view/ccg/018/a/index.xhtml");
    /**
     * CCG018B
     */
    public static final Program CCG018B = new Program(WebAppId.COM, ProgramIdConsts.CCG018B, "CCG018_40",
            "/view/ccg/018/b/index.xhtml");
    /**
     * CCG018C
     */
    public static final Program CCG018C = new Program(WebAppId.COM, ProgramIdConsts.CCG018C, "CCG018_41",
            "/view/ccg/018/c/index.xhtml");
    /**
     * CCG027A
     */
    public static final Program CCG027A = new Program(WebAppId.COM, ProgramIdConsts.CCG027A, "CCG027_1",
            "/view/ccg/027/a/index.xhtml");

    /**
     * CCG030A
     */
    public static final Program CCG030A = new Program(WebAppId.COM, ProgramIdConsts.CCG030A, "CCG030_1",
            "/view/ccg/030/a/index.xhtml");

    /**
     * CCG030B
     */
    public static final Program CCG030B = new Program(WebAppId.COM, ProgramIdConsts.CCG030B, "CCG030_4",
            "/view/ccg/030/b/index.xhtml");

    /**
     * CCG031A
     */
    public static final Program CCG031A = new Program(WebAppId.COM, ProgramIdConsts.CCG031A, "CCG031_1",
            "/view/ccg/031/a/index.xhtml");
    /**
     * CCG031B
     */
    public static final Program CCG031B = new Program(WebAppId.COM, ProgramIdConsts.CCG031B, "CCG031_2",
            "/view/ccg/031/b/index.xhtml");
    /**
     * CCG031C
     */
    public static final Program CCG031C = new Program(WebAppId.COM, ProgramIdConsts.CCG031C, "CCG031_3",
            "/view/ccg/031/c/index.xhtml");
    
    /**
     * CCG034
     */
    public static final Program CCG034A = new Program(WebAppId.COM, ProgramIdConsts.CCG034A, "CCG034_1",
    		"/view/ccg/034/a/index.xhtml");
    public static final Program CCG034B = new Program(WebAppId.COM, ProgramIdConsts.CCG034B, "CCG034_2",
    		"/view/ccg/034/b/index.xhtml");
    public static final Program CCG034C = new Program(WebAppId.COM, ProgramIdConsts.CCG034C, "CCG034_3",
    		"/view/ccg/034/c/index.xhtml");
    public static final Program CCG034D = new Program(WebAppId.COM, ProgramIdConsts.CCG034D, "CCG034_4",
    		"/view/ccg/034/d/index.xhtml");
    public static final Program CCG034E = new Program(WebAppId.COM, ProgramIdConsts.CCG034E, "CCG034_5",
    		"/view/ccg/034/e/index.xhtml");
    public static final Program CCG034F = new Program(WebAppId.COM, ProgramIdConsts.CCG034F, "CCG034_6",
    		"/view/ccg/034/f/index.xhtml");
    public static final Program CCG034G = new Program(WebAppId.COM, ProgramIdConsts.CCG034G, "CCG034_7",
    		"/view/ccg/034/g/index.xhtml");
    public static final Program CCG034H = new Program(WebAppId.COM, ProgramIdConsts.CCG034H, "CCG034_8",
    		"/view/ccg/034/h/index.xhtml");
    public static final Program CCG034I = new Program(WebAppId.COM, ProgramIdConsts.CCG034I, "CCG034_9",
    		"/view/ccg/034/i/index.xhtml");
    public static final Program CCG034J = new Program(WebAppId.COM, ProgramIdConsts.CCG034J, "CCG034_10",
    		"/view/ccg/034/j/index.xhtml");
    
    /**
     * CDL024
     */
    public static final Program CDL024 = new Program(WebAppId.COM, ProgramIdConsts.CDL024, "CDL024_8",
            "/view/cdl/024/index.xhtml");
    /**
     * CDL022A
     */
    public static final Program CDL022A = new Program(WebAppId.COM, ProgramIdConsts.CDL022A, "CDL022_1",
            "/view/cdl/022/a/index.xhtml");

    /**
     * CDL028A
     */
    public static final Program CDL028A = new Program(WebAppId.COM, ProgramIdConsts.CDL028A, "CDL028_1",
            "/view/cdl/028/a/index.xhtml");
    
    /**
     * CDL028A
     */
    public static final Program CDL015A = new Program(WebAppId.COM, ProgramIdConsts.CDL015A, "CDL015_1",
            "/view/cdl/015/a/index.xhtml");
    
    /**
     * CPS007A
     */
    public static final Program CPS007A = new Program(WebAppId.COM, ProgramIdConsts.CPS007A, "CPS007_1",
            "/view/cps/007/a/index.xhtml");

    /**
     * CPS007B
     */
    public static final Program CPS007B = new Program(WebAppId.COM, ProgramIdConsts.CPS007B, "CPS007_2",
            "/view/cps/007/b/index.xhtml");

    /**
     * CPS008A
     */
    public static final Program CPS008A = new Program(WebAppId.COM, ProgramIdConsts.CPS008A, "CPS008_1",
            "/view/cps/008/a/index.xhtml");

    /**
     * CPS008D
     */
    public static final Program CPS008D = new Program(WebAppId.COM, ProgramIdConsts.CPS008D, "CPS008_41",
            "/view/cps/008/b/index.xhtml");
    /**
     * CPS008C
     */
    public static final Program CPS008C = new Program(WebAppId.COM, ProgramIdConsts.CPS008C, "CPS008_38",
            "/view/cps/008/c/index.xhtml");
    
    
    
    public static final Program CMM040A = new Program(WebAppId.COM, ProgramIdConsts.CMM040A, "CMM040_1",
            "/view/cmm/040/a/index.xhtml");
    
    
    public static final Program CMM040B = new Program(WebAppId.COM, ProgramIdConsts.CMM040B, "CMM040_2",
            "/view/cmm/040/b/index.xhtml");
    
    /**
     * CMM044A
     */
    public static final Program CMM044A = new Program(WebAppId.COM, ProgramIdConsts.CMM044A, "CMM044_1",
            "/view/cmm/044/a/index.xhtml");
    public static final Program CMM044C = new Program(WebAppId.COM, ProgramIdConsts.CMM044C, "CMM044_3",
            "/view/cmm/044/c/index.xhtml");
    public static final Program CMM044D = new Program(WebAppId.COM, ProgramIdConsts.CMM044D, "CMM044_33",
            "/view/cmm/044/d/index.xhtml");

    /**
     * CPS005A
     */
    public static final Program CPS005A = new Program(WebAppId.COM, ProgramIdConsts.CPS005A, "CPS005_36",
            "/view/cps/005/a/index.xhtml");

    /**
     * CPS005B
     */
    public static final Program CPS005B = new Program(WebAppId.COM, ProgramIdConsts.CPS005B, "CPS005_37",
            "/view/cps/005/b/index.xhtml");

    /**
     * CPS001A
     */
    public static final Program CPS001A = new Program(WebAppId.COM, ProgramIdConsts.CPS001A, "CPS001_1",
            "/view/cps/001/a/index.xhtml");

    /**
     * CPS001B
     */
    public static final Program CPS001B = new Program(WebAppId.COM, ProgramIdConsts.CPS001B, "CPS001_2",
            "/view/cps/001/b/index.xhtml");

    /**
     * CPS001C
     */
    public static final Program CPS001C = new Program(WebAppId.COM, ProgramIdConsts.CPS001C, "CPS001_3",
            "/view/cps/001/c/index.xhtml");

    /**
     * CPS001D
     */
    public static final Program CPS001D = new Program(WebAppId.COM, ProgramIdConsts.CPS001D, "CPS001_94",
            "/view/cps/001/d/index.xhtml");

    /**
     * CPS001E
     */
    public static final Program CPS001E = new Program(WebAppId.COM, ProgramIdConsts.CPS001E, "CPS001_95",
            "/view/cps/001/e/index.xhtml");

    /**
     * CPS001F
     */
    public static final Program CPS001F = new Program(WebAppId.COM, ProgramIdConsts.CPS001F, "CPS001_96",
            "/view/cps/001/f/index.xhtml");

    /**
     * CPS001H
     */
    public static final Program CPS001H = new Program(WebAppId.COM, ProgramIdConsts.CPS001H, "CPS001_112",
            "/view/cps/001/h/index.xhtml");

    /**
     * CPS001I
     */
    public static final Program CPS001I = new Program(WebAppId.COM, ProgramIdConsts.CPS001I, "CPS001_113",
            "/view/cps/001/i/index.xhtml");

    /**
     * KSP001A
     */
    public static final Program KSP001A = new Program(WebAppId.AT, ProgramIdConsts.KSP001A, "KSP001_1",
            "/view/ksp/001/a/index.xhtml");

    /**
     * KSP001B
     */
    public static final Program KSP001B = new Program(WebAppId.AT, ProgramIdConsts.KSP001B, "KSP001_2",
            "/view/ksp/001/b/index.xhtml");

    /**
     * KSP001C
     */
    public static final Program KSP001C = new Program(WebAppId.AT, ProgramIdConsts.KSP001C, "KSP001_3",
            "/view/ksp/001/c/index.xhtml");

    /**
     * KSP001D
     */
    public static final Program KSP001D = new Program(WebAppId.AT, ProgramIdConsts.KSP001D, "KSP001_4",
            "/view/ksp/001/d/index.xhtml");

    /**
     * KSP001E
     */
    public static final Program KSP001E = new Program(WebAppId.AT, ProgramIdConsts.KSP001E, "KSP001_5",
            "/view/ksp/001/e/index.xhtml");

    /**
     * CPS003A
     */
    public static final Program CPS003A = new Program(WebAppId.COM, ProgramIdConsts.CPS003A, "CPS003_1",
            "/view/cps/003/a/index.xhtml");

    /**
     * CPS003B
     */
    public static final Program CPS003B = new Program(WebAppId.COM, ProgramIdConsts.CPS003B, "CPS003_2",
            "/view/cps/003/b/index.xhtml");

    /**
     * CPS003C
     */
    public static final Program CPS003C = new Program(WebAppId.COM, ProgramIdConsts.CPS003C, "CPS003_3",
            "/view/cps/003/c/index.xhtml");

    /**
     * CPS003D
     */
    public static final Program CPS003D = new Program(WebAppId.COM, ProgramIdConsts.CPS003D, "CPS003_4",
            "/view/cps/003/d/index.xhtml");

    /**
     * CPS003E
     */
    public static final Program CPS003E = new Program(WebAppId.COM, ProgramIdConsts.CPS003E, "CPS003_5",
            "/view/cps/003/e/index.xhtml");

    /**
     * CPS003F
     */
    public static final Program CPS003F = new Program(WebAppId.COM, ProgramIdConsts.CPS003F, "CPS003_6",
            "/view/cps/003/f/index.xhtml");

    /**
     * CPS003G
     */
    public static final Program CPS003G = new Program(WebAppId.COM, ProgramIdConsts.CPS003G, "CPS003_107",
            "/view/cps/003/g/index.xhtml");

    /**
     * KMK011A
     */
    public static final Program KMK011A = new Program(WebAppId.AT, ProgramIdConsts.KMK011A, "KMK011_1",
            "/view/kmk/011/a/index.xhtml");
    /**
     * KMK011B
     */
    public static final Program KMK011B = new Program(WebAppId.AT, ProgramIdConsts.KMK011B, "KMK011_42",
            "/view/kmk/011/b/index.xhtml");
    /**
     * KMK011C
     */
    public static final Program KMK011C = new Program(WebAppId.AT, ProgramIdConsts.KMK011C, "KMK011_15",
            "/view/kmk/011/c/index.xhtml");
    /**
     * KMK011D
     */
    public static final Program KMK011D = new Program(WebAppId.AT, ProgramIdConsts.KMK011D, "KMK011_66",
            "/view/kmk/011/d/index.xhtml");

    /**
     * KMK011E
     */
    public static final Program KMK011E = new Program(WebAppId.AT, ProgramIdConsts.KMK011E, "KMK011_67",
            "/view/kmk/011/e/index.xhtml");

    /**
     * KMK011F
     */
    public static final Program KMK011F = new Program(WebAppId.AT, ProgramIdConsts.KMK011F, "KMK011_68",
            "/view/kmk/011/f/index.xhtml");

    /**
     * KMK011G
     */
    public static final Program KMK011G = new Program(WebAppId.AT, ProgramIdConsts.KMK011G, "KMK011_69",
            "/view/kmk/011/g/index.xhtml");

    /**
     * KMK011H
     */
    public static final Program KMK011H = new Program(WebAppId.AT, ProgramIdConsts.KMK011H, "KMK011_44",
            "/view/kmk/011/h/index.xhtml");
    /**
     * KMK011I
     */
    public static final Program KMK011I = new Program(WebAppId.AT, ProgramIdConsts.KMK011I, "KMK011_75",
            "/view/kmk/011/i/index.xhtml");


    /**
     * KMK017A
     */
    public static final Program KMK017A = new Program(WebAppId.AT, ProgramIdConsts.KMK017A, "KMK017_1",
        "/view/kmk/017/a/index.xhtml");

    /**
     * KML001A
     */
    public static final Program KML001A = new Program(WebAppId.AT, ProgramIdConsts.KML001A, "KML001_29",
            "/view/kml/001/a/index.xhtml");
    /**
     * KML001B
     */
    public static final Program KML001B = new Program(WebAppId.AT, ProgramIdConsts.KML001B, "KML001_3",
            "/view/kml/001/b/index.xhtml");
    /**
     * KML001C
     */
    public static final Program KML001C = new Program(WebAppId.AT, ProgramIdConsts.KML001C, "KML001_55",
            "/view/kml/001/c/index.xhtml");
    /**
     * KML001D
     */
    public static final Program KML001D = new Program(WebAppId.AT, ProgramIdConsts.KML001D, "KML001_56",
            "/view/kml/001/d/index.xhtml");

    /**
     * KMT005A
     */
    public static final Program KMT005A = new Program(WebAppId.AT, ProgramIdConsts.KMT005A, "KMT005_1",
            "/view/kmt/005/a/index.xhtml");

    /**
     * KMT011A
     */
    public static final Program KMT011A = new Program(WebAppId.AT, ProgramIdConsts.KMT011A, "KMT011_1",
            "/view/kmt/011/a/index.xhtml");

    /**
     * KMT014A
     */
    public static final Program KMT014A = new Program(WebAppId.AT, ProgramIdConsts.KMT014A, "KMT014_1",
            "/view/kmt/014/a/index.xhtml");
    /**
     * KMT009A
     */
    public static final Program KMT009A = new Program(WebAppId.AT, ProgramIdConsts.KMT009A, "KMT009_1",
            "/view/kmt/009/a/index.xhtml");

    /**
     * KMT010A
     */
    public static final Program KMT010A = new Program(WebAppId.AT, ProgramIdConsts.KMT010A, "KMT010_1",
            "/view/kmt/010/a/index.xhtml");
    /**
     * KMT001A
     */
    public static final Program KMT001A = new Program(WebAppId.AT, ProgramIdConsts.KMT001A, "KMT001_1",
            "/view/kmt/001/a/index.xhtml");

    /**
     * KDL001A
     */
    public static final Program KDL001A = new Program(WebAppId.AT, ProgramIdConsts.KDL001A, "KDL001_1",
            "/view/kdl/001/a/index.xhtml");
    /**
     * KDL002A
     */
    public static final Program KDL002A = new Program(WebAppId.AT, ProgramIdConsts.KDL002A, "KDL002_1",
            "/view/kdl/002/a/index.xhtml");
    /**
     * KDL005A
     */
    public static final Program KDL005A = new Program(WebAppId.AT, ProgramIdConsts.KDL005A, "KDL005_1",
            "/view/kdl/005/a/index.xhtml");
    /**
     * KDL009A
     */
    public static final Program KDL009A = new Program(WebAppId.AT, ProgramIdConsts.KDL009A, "KDL009_1",
            "/view/kdl/009/a/index.xhtml");
    /**
     * KDL007A
     */
    public static final Program KDL007A = new Program(WebAppId.AT, ProgramIdConsts.KDL007A, "KDL007_7",
            "/view/kdl/007/a/index.xhtml");
    /**
     * KDL010A
     */
    public static final Program KDL010A = new Program(WebAppId.AT, ProgramIdConsts.KDL010A, "KDL010_10",
            "/view/kdl/010/a/index.xhtml");
    /**
     * KDL012
     */
    public static final Program KDL012 = new Program(WebAppId.AT, ProgramIdConsts.KDL012, "KDL012_1",
            "/view/kdl/012/index.xhtml");
    
    /**
     * KDL013
     */
    public static final Program KDL013 = new Program(WebAppId.AT, ProgramIdConsts.KDL013, "KDL013_8",
            "/view/kdl/013/a/index.xhtml");
    /**
     * 
     */
    /**
     * KDL017A
     */
    public static final Program KDL017A = new Program(WebAppId.AT, ProgramIdConsts.KDL017A, "KDL017_1",
            "/view/kdl/017/a/single.xhtml");
    public static final Program KDL017B = new Program(WebAppId.AT, ProgramIdConsts.KDL017B, "KDL017_1",
            "/view/kdl/017/a/multiple.xhtml");

    /**
     * KDL021A
     */
    public static final Program KDL021A = new Program(WebAppId.AT, ProgramIdConsts.KDL021A, "KDL021_1",
            "/view/kdl/021/a/index.xhtml");
    /**
     * KDL024A
     */
    public static final Program KDL024A = new Program(WebAppId.AT, ProgramIdConsts.KDL024A, "KDL024_13",
            "/view/kdl/024/a/index.xhtml");

    /**
     * KDL014A
     */
    public static final Program KDL014A = new Program(WebAppId.AT, ProgramIdConsts.KDL014A, "KDL014_1",
            "/view/kdl/014/a/index.xhtml");

    public static final Program KDL044 = new Program(WebAppId.AT, ProgramIdConsts.KDL044, "KDL044_12",
            "/view/kdl/044/a/index.xhtml");

    /**
     * KDL045A
     */
    public static final Program KDL045A = new Program(WebAppId.AT, ProgramIdConsts.KDL045A, "KDL045_1",
            "/view/kdl/045/a/index.xhtml");

    /**
     * KDL046A
     */
    public static final Program KDL046A = new Program(WebAppId.AT, ProgramIdConsts.KDL046A, "KDL046_1",
            "/view/kdl/046/a/index.xhtml");

    /**
     * KDL014B
     */
    public static final Program KDL014B = new Program(WebAppId.AT, ProgramIdConsts.KDL014B, "KDL014_1",
            "/view/kdl/014/b/index.xhtml");

    /**
     * KDL020A
     */

    public static final Program KDL020A = new Program(WebAppId.AT, ProgramIdConsts.KDL020A, "KDL020_1",
            "/view/kdl/020/a/index.xhtml");
    public static final Program KDL020B = new Program(WebAppId.AT, ProgramIdConsts.KDL020B, "KDL020_1",
            "/view/kdl/020/a/multi.xhtml");
    public static final Program KDL048 = new Program(WebAppId.AT, ProgramIdConsts.KDL048, "KDL048_1",
            "/view/kdl/048/index.xhtml");
    
    public static final Program KDL049A = new Program(WebAppId.AT, ProgramIdConsts.KDL049A, "KDL049_1",
            "/view/kdl/049/a/index.xhtml");

    public static final Program KDL035A = new Program(WebAppId.AT, ProgramIdConsts.KDL035A, "KDL035_13",
            "/view/kdl/035/a/index.xhtml");

    public static final Program KDL036A = new Program(WebAppId.AT, ProgramIdConsts.KDL036A, "KDL036_13",
            "/view/kdl/036/a/index.xhtml");
    ////////////////////
    /**
     * KBT002A
     */
    public static final Program KBT002A = new Program(WebAppId.AT, ProgramIdConsts.KBT002A, "KBT002_114",
            "/view/kbt/002/a/index.xhtml");
    /**
     * KBT002B
     */
    public static final Program KBT002B = new Program(WebAppId.AT, ProgramIdConsts.KBT002B, "KBT002_115",
            "/view/kbt/002/b/index.xhtml");
    /**
     * KBT002C
     */
    public static final Program KBT002C = new Program(WebAppId.AT, ProgramIdConsts.KBT002C, "KBT002_116",
            "/view/kbt/002/c/index.xhtml");
    /**
     * KBT002D
     */
    public static final Program KBT002D = new Program(WebAppId.AT, ProgramIdConsts.KBT002D, "KBT002_117",
            "/view/kbt/002/d/index.xhtml");
    /**
     * KBT002E
     */
    public static final Program KBT002E = new Program(WebAppId.AT, ProgramIdConsts.KBT002E, "KBT002_118",
            "/view/kbt/002/e/index.xhtml");
    /**
     * KBT002F
     */
    public static final Program KBT002F = new Program(WebAppId.AT, ProgramIdConsts.KBT002F, "KBT002_125",
            "/view/kbt/002/f/index.xhtml");
    /**
     * KBT002G
     */
    public static final Program KBT002G = new Program(WebAppId.AT, ProgramIdConsts.KBT002G, "KBT002_148",
            "/view/kbt/002/g/index.xhtml");
    /**
     * KBT002H
     */
    public static final Program KBT002H = new Program(WebAppId.AT, ProgramIdConsts.KBT002H, "KBT002_150",
            "/view/kbt/002/h/index.xhtml");
    /**
     * KBT002I
     */
    public static final Program KBT002I = new Program(WebAppId.AT, ProgramIdConsts.KBT002I, "KBT002_190",
            "/view/kbt/002/i/index.xhtml");
    /**
     * KBT002K
     */
    public static final Program KBT002K = new Program(WebAppId.AT, ProgramIdConsts.KBT002K, "KBT002_270",
            "/view/kbt/002/k/index.xhtml");
    /**
     * KBT002L
     */
    public static final Program KBT002L = new Program(WebAppId.AT, ProgramIdConsts.KBT002L, "KBT002_324",
            "/view/kbt/002/l/index.xhtml");

    /**
     * KBT002J
     */
    public static final Program KBT002J = new Program(WebAppId.AT, ProgramIdConsts.KBT002J, "KBT002_273",
    		"/view/kbt/002/j/index.xhtml");
    
    /**
     * KDW001A
     */
    public static final Program KDW001A = new Program(WebAppId.AT, ProgramIdConsts.KDW001A, "KDW001_105",
            "/view/kdw/001/a/index.xhtml");
    /**
     * KDW001B
     */
    public static final Program KDW001B = new Program(WebAppId.AT, ProgramIdConsts.KDW001B, "KDW001_105",
            "/view/kdw/001/b/index.xhtml");
    /**
     * KDW001C
     */
    public static final Program KDW001C = new Program(WebAppId.AT, ProgramIdConsts.KDW001C, "KDW001_105",
            "/view/kdw/001/c/index.xhtml");
    /**
     * KDW001D
     */
    public static final Program KDW001D = new Program(WebAppId.AT, ProgramIdConsts.KDW001D, "KDW001_105",
            "/view/kdw/001/d/index.xhtml");
    /**
     * KDW001E
     */
    public static final Program KDW001E = new Program(WebAppId.AT, ProgramIdConsts.KDW001E, "KDW001_105",
            "/view/kdw/001/e/index.xhtml");
    /**
     * KDW001F
     */
    public static final Program KDW001F = new Program(WebAppId.AT, ProgramIdConsts.KDW001F, "KDW001_105",
            "/view/kdw/001/f/index.xhtml");
    /**
     * KDW001G
     */
    public static final Program KDW001G = new Program(WebAppId.AT, ProgramIdConsts.KDW001G, "KDW001_106",
            "/view/kdw/001/g/index.xhtml");
    /**
     * KDW001H
     */
    public static final Program KDW001H = new Program(WebAppId.AT, ProgramIdConsts.KDW001H, "KDW001_107",
            "/view/kdw/001/h/index.xhtml");
    /**
     * KDW001I
     */
    public static final Program KDW001I = new Program(WebAppId.AT, ProgramIdConsts.KDW001I, "KDW001_108",
            "/view/kdw/001/i/index.xhtml");
    /**
     * KDW001J
     */
    public static final Program KDW001J = new Program(WebAppId.AT, ProgramIdConsts.KDW001J, "KDW001_105",
            "/view/kdw/001/j/index.xhtml");
    ///////////
    public static final Program KDW006A = new Program(WebAppId.AT, ProgramIdConsts.KDW006A, "",
            "/view/kdw/006/a/index.xhtml");
    public static final Program KDW006B = new Program(WebAppId.AT, ProgramIdConsts.KDW006B, "KDW006_20",
            "/view/kdw/006/b/index.xhtml");
    public static final Program KDW006C = new Program(WebAppId.AT, ProgramIdConsts.KDW006C, "KDW006_21",
            "/view/kdw/006/c/index.xhtml");
    public static final Program KDW006D = new Program(WebAppId.AT, ProgramIdConsts.KDW006D, "KDW006_22",
            "/view/kdw/006/d/index.xhtml");
    public static final Program KDW006E = new Program(WebAppId.AT, ProgramIdConsts.KDW006E, "KDW006_23",
            "/view/kdw/006/e/index.xhtml");
    public static final Program KDW006G = new Program(WebAppId.AT, ProgramIdConsts.KDW006G, "KDW006_71",
            "/view/kdw/006/g/index.xhtml");
    public static final Program KDW006I = new Program(WebAppId.AT, ProgramIdConsts.KDW006I, "KDW006_232",
            "/view/kdw/006/i/index.xhtml");
    public static final Program KDW006H = new Program(WebAppId.AT, ProgramIdConsts.KDW006H, "KDW006_305",
            "/view/kdw/006/h/index.xhtml");
    public static final Program KDW006J = new Program(WebAppId.AT, ProgramIdConsts.KDW006J, "KDW006_340",
            "/view/kdw/006/j/index.xhtml");
    public static final Program KDW006K = new Program(WebAppId.AT, ProgramIdConsts.KDW006K, "KDW006_341",
            "/view/kdw/006/k/index.xhtml");
    public static final Program KDW006L = new Program(WebAppId.AT, ProgramIdConsts.KDW006L, "KDW006_342",
            "/view/kdw/006/l/index.xhtml");

    /**
     * KDW009A
     */
    public static final Program KDW009A = new Program(WebAppId.AT, ProgramIdConsts.KDW009A, "KDW009_1",
            "/view/kdw/009/a/index.xhtml");

    /**
     * KDW010A
     */
    public static final Program KDW010A = new Program(WebAppId.AT, ProgramIdConsts.KDW010A, "KDW010_1",
            "/view/kdw/010/a/index.xhtml");

    /**
     * KDW013A
     */
    public static final Program KDW013A = new Program(WebAppId.AT, ProgramIdConsts.KDW013A, null,
            "/view/kdw/013/a/index.xhtml");
    
    /**
     * KDW013K
     */
    public static final Program KDW013K = new Program(WebAppId.AT, ProgramIdConsts.KDW013K, "KDW013_62",
            "/view/kdw/013/k/index.xhtml");

    /**
     * KDW013D
     */
    public static final Program KDW013D = new Program(WebAppId.AT, ProgramIdConsts.KDW013D, "KDW013_42",
            "/view/kdw/013/d/index.xhtml");
    
    /**
     * KDW013E
     */
    public static final Program KDW013E = new Program(WebAppId.AT, ProgramIdConsts.KDW013E, "KDW013_91",
            "/view/kdw/013/e/index.xhtml");
    
    /**
     * KDW013H
     */
    public static final Program KDW013H = new Program(WebAppId.AT, ProgramIdConsts.KDW013H, "KDW013_94",
            "/view/kdw/013/h/index.xhtml");

    /**
     * KSM002A
     */
    public static final Program KSM002A = new Program(WebAppId.AT, ProgramIdConsts.KSM002A, "KSM002_1",
            "/view/ksm/002/a/index.xhtml");

    /**
     * KSM002B
     */
    public static final Program KSM002B = new Program(WebAppId.AT, ProgramIdConsts.KSM002B, "KSM002_1",
            "/view/ksm/002/b/index.xhtml");

    /**
     * KSM002C
     */
    public static final Program KSM002C = new Program(WebAppId.AT, ProgramIdConsts.KSM002C, "KSM002_7",
            "/view/ksm/002/c/index.xhtml");

    /**
     * KSM002D
     */
    public static final Program KSM002D = new Program(WebAppId.AT, ProgramIdConsts.KSM002D, "KSM002_2",
            "/view/ksm/002/d/index.xhtml");
    /**
     * KSM002E
     */
    public static final Program KSM002E = new Program(WebAppId.AT, ProgramIdConsts.KSM002E, "KSM002_3",
            "/view/ksm/002/e/index.xhtml");

    /**
     * KSM004A
     */
    public static final Program KSM004A = new Program(WebAppId.AT, ProgramIdConsts.KSM004A, "KSM004_55",
            "/view/ksm/004/a/index.xhtml");

    /**
     * KDL058A
     */
    public static final Program KDL058A = new Program(WebAppId.AT, ProgramIdConsts.KDL058A, "KDL058_1",
            "/view/kdl/058/a/index.xhtml");

    /**
     * KSM004D
     */
    public static final Program KSM004D = new Program(WebAppId.AT, ProgramIdConsts.KSM004D, "KSM004_57",
            "/view/ksm/004/d/index.xhtml");
    /**
     * KSM004E
     */
    public static final Program KSM004E = new Program(WebAppId.AT, ProgramIdConsts.KSM004E, "KSM004_58",
            "/view/ksm/004/e/index.xhtml");

    /**
     * KSM004F
     */
    public static final Program KSM004F = new Program(WebAppId.AT, ProgramIdConsts.KSM004F, "KSM004_116",
            "/view/ksm/004/f/index.xhtml");

    /**
     * KSM008A
     */
    public static final Program KSM008A = new Program(WebAppId.AT, ProgramIdConsts.KSM008A, "KSM008_1",
            "/view/ksm/008/a/index.xhtml");

    /**
     * KSM008B
     */
    public static final Program KSM008B = new Program(WebAppId.AT, ProgramIdConsts.KSM008B, "KSM008_2",
            "/view/ksm/008/b/index.xhtml");

    /**
     * KSM008C
     */
    public static final Program KSM008C = new Program(WebAppId.AT, ProgramIdConsts.KSM008C, "KSM008_3",
            "/view/ksm/008/c/index.xhtml");

    /**
     * KSM008D
     */
    public static final Program KSM008D = new Program(WebAppId.AT, ProgramIdConsts.KSM008D, "KSM008_5",
            "/view/ksm/008/d/index.xhtml");

    /**
     * KSM008F
     */
    public static final Program KSM008F = new Program(WebAppId.AT, ProgramIdConsts.KSM008F, "KSM008_004",
            "/view/ksm/008/f/index.xhtml");

    /**
     * KSM008G
     */
    public static final Program KSM008G = new Program(WebAppId.AT, ProgramIdConsts.KSM008G, "KSM008_131",
            "/view/ksm/008/g/index.xhtml");

    /**
     * KSU001A
     */
    public static final Program KSU001A = new Program(WebAppId.AT, ProgramIdConsts.KSU001A, "",
            "/view/ksu/001/a/index.xhtml");
    /**
     * KSU001C
     */
    public static final Program KSU001C = new Program(WebAppId.AT, ProgramIdConsts.KSU001C, "",
            "/view/ksu/001/c/index.xhtml");
    /**
     * KSU001D
     */
    public static final Program KSU001D = new Program(WebAppId.AT, ProgramIdConsts.KSU001D, "",
            "/view/ksu/001/d/index.xhtml");
    /**
     * KSU001M
     */
    public static final Program KSU001M = new Program(WebAppId.AT, ProgramIdConsts.KSU001M, "KSU001_3301",
            "/view/ksu/001/m/index.xhtml");
    /**
     * KSU001JB
     */
    public static final Program KSU001JB = new Program(WebAppId.AT, ProgramIdConsts.KSU001JB, "KSU001_147",
            "/view/ksu/001/jb/index.xhtml");
    /**
     * KSU001JC
     */
    public static final Program KSU001JC = new Program(WebAppId.AT, ProgramIdConsts.KSU001JC, "KSU001_148",
            "/view/ksu/001/jc/index.xhtml");
    /**
     * KSU001JD
     */
    public static final Program KSU001JD = new Program(WebAppId.AT, ProgramIdConsts.KSU001JD, "KSU001_152",
            "/view/ksu/001/jd/index.xhtml");
    /**
     * KSU001L
     */
    public static final Program KSU001L = new Program(WebAppId.AT, ProgramIdConsts.KSU001L, "KSU001_1140",
            "/view/ksu/001/l/index.xhtml");

    /**
     * KSU001LA
     */
    public static final Program KSU001LA = new Program(WebAppId.AT, ProgramIdConsts.KSU001LA, "KSU001_3201",
            "/view/ksu/001/la/index.xhtml");
    /**
     * KSU001LX
     */
    public static final Program KSU001LX = new Program(WebAppId.AT, ProgramIdConsts.KSU001LX, "KSU001_1141",
            "/view/ksu/001/lx/index.xhtml");
    /**
     * KSU001N
     */
    public static final Program KSU001N = new Program(WebAppId.AT, ProgramIdConsts.KSU001N, "KSU001_1327",
            "/view/ksu/001/n/index.xhtml");
    /**
     * KSU001U
     */
    public static final Program KSU001U = new Program(WebAppId.AT, ProgramIdConsts.KSU001U, "KSU001_4000",
            "/view/ksu/001/u/index.xhtml");

    /**
     * KSU001G
     */
    public static final Program KSU001G = new Program(WebAppId.AT, ProgramIdConsts.KSU001G, "KSU001_4028",
            "/view/ksu/001/g/index.xhtml");
    /**
     * KSU001R
     */
    public static final Program KSU001R = new Program(WebAppId.AT, ProgramIdConsts.KSU001R, "KSU001_3800",
            "/view/ksu/001/r/index.xhtml");
    /**
     * KSU001O1
     */
    public static final Program KSU001O = new Program(WebAppId.AT, ProgramIdConsts.KSU001O, "KSU001_104",
            "/view/ksu/001/o1/index.xhtml");
    /**
     * KSU001Q
     */
    public static final Program KSU001Q = new Program(WebAppId.AT, ProgramIdConsts.KSU001Q, "KSU001_3701",
            "/view/ksu/001/q/index.xhtml");
    /**
     * KSU002A
     */
    public static final Program KSU002A = new Program(WebAppId.AT, ProgramIdConsts.KSU002A, "KSU002_32",
            "/view/ksu/002/a/index.xhtml");
    /**
     * KSU002B
     */
    public static final Program KSU002B = new Program(WebAppId.AT, ProgramIdConsts.KSU002B, "KSU002_46",
            "/view/ksu/002/b/index.xhtml");
    /**   
     * KSU001SA
     */
    public static final Program KSU001SA = new Program(WebAppId.AT, ProgramIdConsts.KSU001SA, "KSU001_4039",
            "/view/ksu/001/s/a/index.xhtml");
    /**
     * KSU001SA
     */
    public static final Program KSU001SB = new Program(WebAppId.AT, ProgramIdConsts.KSU001SB, "KSU001_4040",
            "/view/ksu/001/s/b/index.xhtml");
    /**
     * /**
     * KSU003A
     */
    public static final Program KSU003A = new Program(WebAppId.AT, ProgramIdConsts.KSU003A, "KSU003_1",
            "/view/ksu/003/a/index.xhtml");

    /**
     * KSU003B
     */
    public static final Program KSU003B = new Program(WebAppId.AT, ProgramIdConsts.KSU003B, "KSU003_71",
            "/view/ksu/003/b/index.xhtml");
    
    /**
     * KSU003C
     */
    public static final Program KSU003C = new Program(WebAppId.AT, ProgramIdConsts.KSU003C, "KSU003_85",
            "/view/ksu/003/c/index.xhtml");
    
    /**
     * KSU003D
     */
    public static final Program KSU003D = new Program(WebAppId.AT, ProgramIdConsts.KSU003D, "KSU003_109",
            "/view/ksu/003/d/index.xhtml");

    /**
     * KSU005A -> KSU001KA
     */
    public static final Program KSU001KA = new Program(WebAppId.AT, ProgramIdConsts.KSU001KA, "KSU001_4076",
            "/view/ksu/001/ka/index.xhtml");

    /**
     * KSU005B
     */
    public static final Program KSU001KB = new Program(WebAppId.AT, ProgramIdConsts.KSU001KB, "KSU001_4077",
            "/view/ksu/001/kb/index.xhtml");

    /**
     * KSU005C
     */
    public static final Program KSU001KC = new Program(WebAppId.AT, ProgramIdConsts.KSU001KC, "KSU001_4078",
            "/view/ksu/001/kc/index.xhtml");

    /**
     * KML002A
     */
    public static final Program KML002A = new Program(WebAppId.AT, ProgramIdConsts.KML002A, "KML002_1",
            "/view/kml/002/a/index.xhtml");
    /**
     * KML002B
     */
    public static final Program KML002B = new Program(WebAppId.AT, ProgramIdConsts.KML002B, "KML002_2",
            "/view/kml/002/b/index.xhtml");
    /**
     * KML002C
     */
    public static final Program KML002C = new Program(WebAppId.AT, ProgramIdConsts.KML002C, "KML002_3",
            "/view/kml/002/c/index.xhtml");
    /**
     * KML002D
     */
    public static final Program KML002D = new Program(WebAppId.AT, ProgramIdConsts.KML002D, "KML002_4",
            "/view/kml/002/d/index.xhtml");
    /**
     * KML002E
     */
    public static final Program KML002E = new Program(WebAppId.AT, ProgramIdConsts.KML002E, "KML002_5",
            "/view/kml/002/e/index.xhtml");
    /**
     * KML002F
     */
    public static final Program KML002F = new Program(WebAppId.AT, ProgramIdConsts.KML002F, "KML002_6",
            "/view/kml/002/f/index.xhtml");
    /**
     * KML002G
     */
    public static final Program KML002G = new Program(WebAppId.AT, ProgramIdConsts.KML002G, "KML002_7",
            "/view/kml/002/g/index.xhtml");
    
    /**
     * KML002H
     */
    public static final Program KML002H = new Program(WebAppId.AT, ProgramIdConsts.KML002H, "KML002_165",
            "/view/kml/002/h/index.xhtml");
    
    /**
     * KML002K
     */
    public static final Program KML002K = new Program(WebAppId.AT, ProgramIdConsts.KML002K, "KML002_165",
            "/view/kml/002/k/index.xhtml");
    
    /**
     * KML002L
     */
    public static final Program KML002L = new Program(WebAppId.AT, ProgramIdConsts.KML002L, "KML002_166",
            "/view/kml/002/l/index.xhtml");
    
    /**
     * KML004A
     */
    public static final Program KML004A = new Program(WebAppId.AT, ProgramIdConsts.KML004A, "",
            "/view/kml/004/a/index.xhtml");
    /**
     * KML004B
     */
    public static final Program KML004B = new Program(WebAppId.AT, ProgramIdConsts.KML004B, "KML004_52",
            "/view/kml/004/b/index.xhtml");
    /**
     * KML004D
     */
    public static final Program KML004D = new Program(WebAppId.AT, ProgramIdConsts.KML004D, "KML004_53",
            "/view/kml/004/d/index.xhtml");

    /**
     * The Constant KDL003.
     */
    public static final Program KDL003A = new Program(WebAppId.AT, ProgramIdConsts.KDL003A, "KDL003_1",
            "/view/kdl/003/a/index.xhtml"); 

    /**
     * The Constant KSM006.
     */
    public static final Program KSM006A = new Program(WebAppId.AT, ProgramIdConsts.KSM006A, "KSM006_1",
            "/view/ksm/006/a/index.xhtml");

    /**
     * The Constant KSM003.
     */
    public static final Program KSM003A = new Program(WebAppId.AT, ProgramIdConsts.KSM003A, "KSM003_1",
            "/view/ksm/003/a/index.xhtml");

    /**
     * The Constant KSM005A.
     */
    public static final Program KSM005A = new Program(WebAppId.AT, ProgramIdConsts.KSM005A, "KSM005_38",
            "/view/ksm/005/a/index.xhtml");

    /**
     * The Constant KSM005B.
     */
    public static final Program KSM005B = new Program(WebAppId.AT, ProgramIdConsts.KSM005B, "KSM005_41",
            "/view/ksm/005/b/index.xhtml");

    /**
     * The Constant KSM005C.
     */
    public static final Program KSM005C = new Program(WebAppId.AT, ProgramIdConsts.KSM005C, "KSM005_39",
            "/view/ksm/005/c/index.xhtml");

    /**
     * The Constant KSM005E.
     */
    public static final Program KSM005E = new Program(WebAppId.AT, ProgramIdConsts.KSM005E, "KSM005_41",
            "/view/ksm/005/e/index.xhtml");

    /**
     * The Constant KSM005F.
     */
    public static final Program KSM005F = new Program(WebAppId.AT, ProgramIdConsts.KSM005F, "KSM005_42",
            "/view/ksm/005/f/index.xhtml");

    /**
     * The Constant KDL023.
     */
    public static final Program KDL023A = new Program(WebAppId.AT, ProgramIdConsts.KDL023A, "KDL023_1",
            "/view/kdl/023/a/index.xhtml");

    public static final Program KDL023B = new Program(WebAppId.AT, ProgramIdConsts.KDL023B, "KDL023_2",
            "/view/kdl/023/b/index.xhtml");

    /**
     * The Constant KMK009.
     */
    public static final Program KMK009A = new Program(WebAppId.AT, ProgramIdConsts.KMK009A, null,
            "/view/kmk/009/a/index.xhtml");

    /**
     * The Constant KSU006.
     */
    public static final Program KSU006A = new Program(WebAppId.AT, ProgramIdConsts.KSU006A, "KSU006_321",
            "/view/ksu/006/a/index.xhtml");

    public static final Program KSU006B = new Program(WebAppId.AT, ProgramIdConsts.KSU006B, "KSU006_322",
            "/view/ksu/006/b/index.xhtml");

    public static final Program KSU006C = new Program(WebAppId.AT, ProgramIdConsts.KSU006C, "KSU006_323",
            "/view/ksu/006/c/index.xhtml");

    /**
     * The Constant CCG007.
     */
    public static final Program CCG007A = new Program(WebAppId.COM, ProgramIdConsts.CCG007A, "CCG007_51",
            "/view/ccg/007/a/index.xhtml", ProgramIdConsts.CCGS07A);

    /**
     * The Constant CCG007B.
     */
    public static final Program CCG007B = new Program(WebAppId.COM, ProgramIdConsts.CCG007B, "CCG007_52",
            "/view/ccg/007/b/index.xhtml");

    /**
     * The Constant CCG007C.
     */
    public static final Program CCG007C = new Program(WebAppId.COM, ProgramIdConsts.CCG007C, "CCG007_53",
            "/view/ccg/007/c/index.xhtml");

    /**
     * The Constant CCG007D.
     */
    public static final Program CCG007D = new Program(WebAppId.COM, ProgramIdConsts.CCG007D, "CCG007_54",
            "/view/ccg/007/d/index.xhtml", ProgramIdConsts.CCGS07B);

    /**
     * The Constant CCG007E.
     */
    public static final Program CCG007E = new Program(WebAppId.COM, ProgramIdConsts.CCG007E, "CCG007_55",
            "/view/ccg/007/e/index.xhtml", ProgramIdConsts.CCGS07C);

    /**
     * The Constant CCG007F.
     */
    public static final Program CCG007F = new Program(WebAppId.COM, ProgramIdConsts.CCG007F, "CCG007_56",
            "/view/ccg/007/f/index.xhtml", ProgramIdConsts.CCGS07D);

    /**
     * The Constant CCG007G.
     */
    public static final Program CCG007G = new Program(WebAppId.COM, ProgramIdConsts.CCG007G, "CCG007_57",
            "/view/ccg/007/g/index.xhtml", ProgramIdConsts.CCGS07F);

    /**
     * The Constant CCG007H.
     */
    public static final Program CCG007H = new Program(WebAppId.COM, ProgramIdConsts.CCG007H, "CCG007_58",
            "/view/ccg/007/h/index.xhtml", ProgramIdConsts.CCGS07E);

    /**
     * The Constant CCG007I.
     */
    public static final Program CCG007I = new Program(WebAppId.COM, ProgramIdConsts.CCG007I, "CCG007_59",
            "/view/ccg/007/i/index.xhtml");

    /**
     * The Constant KMK004A.
     */
    public static final Program KMK004A = new Program(WebAppId.AT, ProgramIdConsts.KMK004A, "KMK004_1",
            "/view/kmk/004/a/index.xhtml");

    public static final Program KMK004B = new Program(WebAppId.AT, ProgramIdConsts.KMK004B, "KMK004_1",
            "/view/kmk/004/b/index.xhtml");

    public static final Program KMK004C = new Program(WebAppId.AT, ProgramIdConsts.KMK004C, "KMK004_1",
            "/view/kmk/004/c/index.xhtml");

    public static final Program KMK004D = new Program(WebAppId.AT, ProgramIdConsts.KMK004D, "KMK004_1",
            "/view/kmk/004/d/index.xhtml");
    
    public static final Program KMK004G = new Program(WebAppId.AT, ProgramIdConsts.KMK004G, "KMK004_357",
            "/view/kmk/004/g/index.xhtml");
    
    public static final Program KMK004H = new Program(WebAppId.AT, ProgramIdConsts.KMK004H, "KMK004_358",
            "/view/kmk/004/h/index.xhtml");
    
    public static final Program KMK004I = new Program(WebAppId.AT, ProgramIdConsts.KMK004I, "KMK004_359",
            "/view/kmk/004/i/index.xhtml");
    
    public static final Program KMK004J = new Program(WebAppId.AT, ProgramIdConsts.KMK004J, "KMK004_360",
            "/view/kmk/004/j/index.xhtml");
    
    public static final Program KMK004K = new Program(WebAppId.AT, ProgramIdConsts.KMK004K, "KMK004_361",
            "/view/kmk/004/k/index.xhtml");
    
    public static final Program KMK004Q = new Program(WebAppId.AT, ProgramIdConsts.KMK004Q, "KMK004_367",
            "/view/kmk/004/q/index.xhtml");
    
    public static final Program KMK004R = new Program(WebAppId.AT, ProgramIdConsts.KMK004R, "KMK004_368",
            "/view/kmk/004/r/index.xhtml");
    
    public static final Program KMK004S = new Program(WebAppId.AT, ProgramIdConsts.KMK004S, "KMK004_369",
            "/view/kmk/004/s/index.xhtml");
    
    public static final Program KMK004L = new Program(WebAppId.AT, ProgramIdConsts.KMK004L, "KMK004_362",
            "/view/kmk/004/l/index.xhtml");
    
    public static final Program KMK004M = new Program(WebAppId.AT, ProgramIdConsts.KMK004M, "KMK004_363",
            "/view/kmk/004/m/index.xhtml");
    
    public static final Program KMK004N = new Program(WebAppId.AT, ProgramIdConsts.KMK004N, "KMK004_364",
            "/view/kmk/004/n/index.xhtml");
    
    public static final Program KMK004O = new Program(WebAppId.AT, ProgramIdConsts.KMK004O, "KMK004_365",
            "/view/kmk/004/o/index.xhtml");
    
    public static final Program KMK004P = new Program(WebAppId.AT, ProgramIdConsts.KMK004P, "KMK004_366",
            "/view/kmk/004/p/index.xhtml");


    /**
     * The Constant KMK004E.
     */
    public static final Program KMK004E = new Program(WebAppId.AT, ProgramIdConsts.KMK004E, "KMK004_2",
            "/view/kmk/004/e/index.xhtml");

    /**
     * The Constant KMK004F.
     */
    public static final Program KMK004F = new Program(WebAppId.AT, ProgramIdConsts.KMK004F, "KMK004_42",
            "/view/kmk/004/f/index.xhtml");
    

    /**
     * The Constant KMK012A.
     */
    public static final Program KMK012A = new Program(WebAppId.AT, ProgramIdConsts.KMK012A, "KMK012_11",
            "/view/kmk/012/a/index.xhtml");

    /**
     * The Constant KMK012D.
     */
    public static final Program KMK012D = new Program(WebAppId.AT, ProgramIdConsts.KMK012D, "KMK012_34",
            "/view/kmk/012/d/index.xhtml");

    /**
     * The Constant KMK012E.
     */
    public static final Program KMK012E = new Program(WebAppId.AT, ProgramIdConsts.KMK012E, "KMK012_43",
            "/view/kmk/012/e/index.xhtml");

    /**
     * The Constant KMK012F.
     */
    public static final Program KMK012F = new Program(WebAppId.AT, ProgramIdConsts.KMK012F, "KMK012_51",
            "/view/kmk/012/f/index.xhtml");

    /**
     * The Constant KMF001A.
     */
    public static final Program KMF001A = new Program(WebAppId.AT, ProgramIdConsts.KMF001A, "KMF001_1",
            "/view/kmf/001/a/index.xhtml");

    /**
     * The Constant KMF001B.
     */
    public static final Program KMF001B = new Program(WebAppId.AT, ProgramIdConsts.KMF001B, "KMF001_2",
            "/view/kmf/001/b/index.xhtml");

    /**
     * The Constant KMF001C.
     */
    public static final Program KMF001C = new Program(WebAppId.AT, ProgramIdConsts.KMF001C, "KMF001_3",
            "/view/kmf/001/c/index.xhtml");

    /**
     * The Constant KMF001D.
     */
    public static final Program KMF001D = new Program(WebAppId.AT, ProgramIdConsts.KMF001D, "KMF001_4",
            "/view/kmf/001/d/index.xhtml");

    /**
     * The Constant KMF001F.
     */
    public static final Program KMF001F = new Program(WebAppId.AT, ProgramIdConsts.KMF001F, "KMF001_6",
            "/view/kmf/001/f/index.xhtml");

    /**
     * The Constant KMF001H.
     */
    public static final Program KMF001H = new Program(WebAppId.AT, ProgramIdConsts.KMF001H, "KMF001_8",
            "/view/kmf/001/h/index.xhtml");

    /**
     * The Constant KMF001J.
     */
    public static final Program KMF001J = new Program(WebAppId.AT, ProgramIdConsts.KMF001J, "KMF001_10",
            "/view/kmf/001/j/index.xhtml");

    /**
     * The Constant KMF001L.
     */
    public static final Program KMF001L = new Program(WebAppId.AT, ProgramIdConsts.KMF001L, "KMF001_12",
            "/view/kmf/001/l/index.xhtml");

    /**
     * The Constant KMF001M.
     */
    public static final Program KMF001M = new Program(WebAppId.AT, ProgramIdConsts.KMF001M, "KMF001_335",
            "/view/kmf/001/m/index.xhtml");
    
    public static final Program KMF001N = new Program(WebAppId.AT, ProgramIdConsts.KMF001N, "KMF001_349",
            "/view/kmf/001/n/index.xhtml");

    /**
     * KMF003A
     */
    public static final Program KMF003A = new Program(WebAppId.AT, ProgramIdConsts.KMF003A, "KMF003_1",
            "/view/kmf/003/a/index.xhtml");

    /**
     * KMF003B
     */
    public static final Program KMF003B = new Program(WebAppId.AT, ProgramIdConsts.KMF003B, "KMF003_2",
            "/view/kmf/003/b/index.xhtml");

    /**
     * KMF004A
     */
    public static final Program KMF004A = new Program(WebAppId.AT, ProgramIdConsts.KMF004A, "KMF004_112",
            "/view/kmf/004/a/index.xhtml");

    /**
     * KMF004B
     */
    public static final Program KMF004B = new Program(WebAppId.AT, ProgramIdConsts.KMF004B, "KMF004_114",
            "/view/kmf/004/b/index.xhtml");

    /**
     * KMF004C
     */
    public static final Program KMF004C = new Program(WebAppId.AT, ProgramIdConsts.KMF004C, "KMF004_115",
            "/view/kmf/004/c/index.xhtml");

    /**
     * KMF004D
     */
    public static final Program KMF004D = new Program(WebAppId.AT, ProgramIdConsts.KMF004D, "KMF004_95",
            "/view/kmf/004/d/index.xhtml");

    /**
     * KMF004E
     */
    public static final Program KMF004E = new Program(WebAppId.AT, ProgramIdConsts.KMF004E, "KMF004_117",
            "/view/kmf/004/e/index.xhtml");

    /**
     * KMF004F
     */
    public static final Program KMF004F = new Program(WebAppId.AT, ProgramIdConsts.KMF004F, "KMF004_118",
            "/view/kmf/004/f/index.xhtml");

    /**
     * KMF004G
     */
    public static final Program KMF004G = new Program(WebAppId.AT, ProgramIdConsts.KMF004G, "KMF004_97",
            "/view/kmf/004/g/index.xhtml");

    /**
     * KMF004H
     */
    public static final Program KMF004H = new Program(WebAppId.AT, ProgramIdConsts.KMF004H, "KMF004_71",
            "/view/kmf/004/h/index.xhtml");

    /**
     * KMF004I
     */
    public static final Program KMF004I = new Program(WebAppId.AT, ProgramIdConsts.KMF004I, "KMF004_96",
            "/view/kmf/004/i/index.xhtml");

    /**
     * KMF004J
     */
    public static final Program KMF004J = new Program(WebAppId.AT, ProgramIdConsts.KMF004J, "KMF004_98",
            "/view/kmf/004/j/index.xhtml");

    /**
     * KMF004X
     */
    public static final Program KMF004X = new Program(WebAppId.AT, ProgramIdConsts.KMF004X, "KMF004_112",
            "/view/kmf/004/x/index.xhtml");

    /**
     * KMK007A
     */
    public static final Program KMK007A = new Program(WebAppId.AT, ProgramIdConsts.KMK007A, "KMK007_63",
            "/view/kmk/007/a/index.xhtml");

    /**
     * KMK007B
     */
    public static final Program KMK007B = new Program(WebAppId.AT, ProgramIdConsts.KMK007B, "KMK007_64",
            "/view/kmk/007/b/index.xhtml");

    /**
     * KMK007C
     */
    public static final Program KMK007C = new Program(WebAppId.AT, ProgramIdConsts.KMK007C, "KMK007_65",
            "/view/kmk/007/c/index.xhtml");

    /**
     * CAS001A
     */
    public static final Program CAS001A = new Program(WebAppId.COM, ProgramIdConsts.CAS001A, "CAS001_1",
            "/view/cas/001/a/index.xhtml");

    /**
     * CAS003A
     */
    public static final Program CAS003A = new Program(WebAppId.COM, ProgramIdConsts.CAS003A, "CAS003_1",
            "/view/cas/003/a/index.xhtml");

    /**
     * CAS001C
     */
    public static final Program CAS001C = new Program(WebAppId.COM, ProgramIdConsts.CAS001C, "CAS001_67",
            "/view/cas/001/c/index.xhtml");

    /**
     * CAS001D
     */
    public static final Program CAS001D = new Program(WebAppId.COM, ProgramIdConsts.CAS001D, "CAS001_66",
            "/view/cas/001/d/index.xhtml");

    /**
     * KMK005A
     */
    public static final Program KMK005A = new Program(WebAppId.AT, ProgramIdConsts.KMK005A, "KMK005_91",
            "/view/kmk/005/a/index.xhtml");

    /**
     * KMK005B
     */
    public static final Program KMK005B = new Program(WebAppId.AT, ProgramIdConsts.KMK005B, "KMK005_5",
            "/view/kmk/005/b/index.xhtml");

    /**
     * KMK005D
     */
    public static final Program KMK005D = new Program(WebAppId.AT, ProgramIdConsts.KMK005D, "KMK005_7",
            "/view/kmk/005/d/index.xhtml");

    /**
     * KMK005E
     */
    public static final Program KMK005E = new Program(WebAppId.AT, ProgramIdConsts.KMK005E, "KMK005_11",
            "/view/kmk/005/e/index.xhtml");

    /**
     * KMK005B
     */
    public static final Program KMK005F = new Program(WebAppId.AT, ProgramIdConsts.KMK005F, "KMK005_13",
            "/view/kmk/005/f/index.xhtml");

    /**
     * KMK005G
     */
    public static final Program KMK005G = new Program(WebAppId.AT, ProgramIdConsts.KMK005G, "KMK005_92",
            "/view/kmk/005/g/index.xhtml");

    /**
     * KMK005H
     */
    public static final Program KMK005H = new Program(WebAppId.AT, ProgramIdConsts.KMK005H, "KMK005_92",
            "/view/kmk/005/h/index.xhtml");

    /**
     * KMK005H
     */
    public static final Program KMK005I = new Program(WebAppId.AT, ProgramIdConsts.KMK005I, "KMK005_92",
            "/view/kmk/005/i/index.xhtml");

    /**
     * KMK005K
     */
    public static final Program KMK005K = new Program(WebAppId.AT, ProgramIdConsts.KMK005K, "KMK005_92",
            "/view/kmk/005/k/index.xhtml");

    /**
     * KMK008A
     */
    public static final Program KMK008A = new Program(WebAppId.AT, ProgramIdConsts.KMK008A, "KMK008_210",
            "/view/kmk/008/a/index.xhtml");

    /**
     * KMK008B
     */
    public static final Program KMK008B = new Program(WebAppId.AT, ProgramIdConsts.KMK008B, "KMK008_211",
            "/view/kmk/008/b/index.xhtml");

    /**
     * KMK008F
     */
    public static final Program KMK008F = new Program(WebAppId.AT, ProgramIdConsts.KMK008F, "KMK008_215",
            "/view/kmk/008/f/index.xhtml");

    /**
     * KMK008G
     */
    public static final Program KMK008G = new Program(WebAppId.AT, ProgramIdConsts.KMK008G, "KMK008_216",
            "/view/kmk/008/g/index.xhtml");

    /**
     * KMK008H
     */
    public static final Program KMK008H = new Program(WebAppId.AT, ProgramIdConsts.KMK008H, "KMK008_217",
            "/view/kmk/008/h/index.xhtml");

    /**
     * KMK008I
     */
    public static final Program KMK008I = new Program(WebAppId.AT, ProgramIdConsts.KMK008I, "KMK008_218",
            "/view/kmk/008/i/index.xhtml");

    /**
     * KMK008J
     */
    public static final Program KMK008J = new Program(WebAppId.AT, ProgramIdConsts.KMK008J, "KMK008_219",
            "/view/kmk/008/j/index.xhtml");

    /**
     * KMK008K
     */
    public static final Program KMK008K = new Program(WebAppId.AT, ProgramIdConsts.KMK008K, "KMK008_50",
            "/view/kmk/008/k/index.xhtml");

    /**
     * KCP006
     */
    public static final Program KCP006A = new Program(WebAppId.AT, ProgramIdConsts.KCP006A, "CCGXX7_1",
            "/view/kcp/006/a/index.xhtml");

    /**
     * KCP006
     */
    public static final Program KCP006B = new Program(WebAppId.AT, ProgramIdConsts.KCP006B, "CCGXX7_2",
            "/view/kcp/006/b/index.xhtml");

    /**
     * KDW002A
     */
    public static final Program KDW002A = new Program(WebAppId.AT, ProgramIdConsts.KDW002A, "KDW002_1",
            "/view/kdw/002/a/index.xhtml");

    /**
     * KDW002B
     */
    public static final Program KDW002B = new Program(WebAppId.AT, ProgramIdConsts.KDW002B, "KDW002_1",
            "/view/kdw/002/b/index.xhtml");

    /**
     * KDW002C
     */
    public static final Program KDW002C = new Program(WebAppId.AT, ProgramIdConsts.KDW002C, "KDW002_1",
            "/view/kdw/002/c/index.xhtml");

    /**
     * The Constant CCG001.
     */
    public static final Program CCG001 = new Program(WebAppId.COM, ProgramIdConsts.CCG001, null,
            "/view/ccg/001/index.xhtml");

    /**
     * The Constant KCP001.
     */
    public static final Program KCP001 = new Program(WebAppId.COM, ProgramIdConsts.KCP001, null,
            "/view/kcp/001/index.xhtml");

    public static final Program KCP002 = new Program(WebAppId.COM, ProgramIdConsts.KCP002, null,
            "/view/kcp/002/index.xhtml");

    public static final Program KCP003 = new Program(WebAppId.COM, ProgramIdConsts.KCP003, null,
            "/view/kcp/003/index.xhtml");

    public static final Program KCP004 = new Program(WebAppId.COM, ProgramIdConsts.KCP004, null,
            "/view/kcp/004/index.xhtml");

    public static final Program KCP005 = new Program(WebAppId.COM, ProgramIdConsts.KCP005, null,
            "/view/kcp/005/index.xhtml");

    public static final Program KCP012 = new Program(WebAppId.COM, ProgramIdConsts.KCP012, null,
            "/view/kcp/012/index.xhtml");

    /**
     * KDW007A
     */
    public static final Program KDW007A = new Program(WebAppId.AT, ProgramIdConsts.KDW007A, "",
            "/view/kdw/007/a/index.xhtml");

    /**
     * KDW007B
     */
    public static final Program KDW007B = new Program(WebAppId.AT, ProgramIdConsts.KDW007B, "KDW007_40",
            "/view/kdw/007/b/index.xhtml");

    /**
     * KDW007C
     */
    public static final Program KDW007C = new Program(WebAppId.AT, ProgramIdConsts.KDW007C, "KDW007_92",
            "/view/kdw/007/c/index.xhtml");

    /**
     * KDW008A
     */
    public static final Program KDW008A = new Program(WebAppId.AT, ProgramIdConsts.KDW008A, "KDW008_1",
            "/view/kdw/008/a/index.xhtml");

    /**
     * KDW008B
     */
    public static final Program KDW008B = new Program(WebAppId.AT, ProgramIdConsts.KDW008B, "KDW008_1",
            "/view/kdw/008/b/index.xhtml");

    /**
     * KDW008C
     */
    public static final Program KDW008C = new Program(WebAppId.AT, ProgramIdConsts.KDW008C, "KDW008_33",
            "/view/kdw/008/c/index.xhtml");

    /**
     * KWR001A
     */
    public static final Program KWR001A = new Program(WebAppId.AT, ProgramIdConsts.KWR001A, "KWR001_1",
            "/view/kwr/001/a/index.xhtml");

    /**
     * KWR001B
     */
    public static final Program KWR001B = new Program(WebAppId.AT, ProgramIdConsts.KWR001B, "KWR001_2",
            "/view/kwr/001/b/index.xhtml");

    /**
     * KWR001C
     */
    public static final Program KWR001C = new Program(WebAppId.AT, ProgramIdConsts.KWR001C, "KWR001_3",
            "/view/kwr/001/c/index.xhtml");

    /**
     * KWR001D
     */
    public static final Program KWR001D = new Program(WebAppId.AT, ProgramIdConsts.KWR001D, "KWR001_4",
            "/view/kwr/001/d/index.xhtml");


    /**
     * KWR006A
     */
    public static final Program KWR006A = new Program(WebAppId.AT, ProgramIdConsts.KWR006A, "KWR006_1",
            "/view/kwr/006/a/index.xhtml");

    /**
     * KWR006C
     */
    public static final Program KWR006C = new Program(WebAppId.AT, ProgramIdConsts.KWR006C, "KWR006_3",
            "/view/kwr/006/c/index.xhtml");

    /**
     * KWR006D
     */
    public static final Program KWR006D = new Program(WebAppId.AT, ProgramIdConsts.KWR006D, "KWR006_4",
            "/view/kwr/006/d/index.xhtml");


    /**
     * CPS006A
     */
    public static final Program CPS006A = new Program(WebAppId.COM, ProgramIdConsts.CPS006A, "CPS006_56",
            "/view/cps/006/a/index.xhtml");

    /**
     * CPS006B
     */
    public static final Program CPS006B = new Program(WebAppId.COM, ProgramIdConsts.CPS006B, "CPS006_57",
            "/view/cps/006/b/index.xhtml");

    /**
     * CPS002A
     */
    public static final Program CPS002A = new Program(WebAppId.COM, ProgramIdConsts.CPS002A, "CPS002_1",
            "/view/cps/002/a/index.xhtml");

    /**
     * CPS002E
     */
    public static final Program CPS002E = new Program(WebAppId.COM, ProgramIdConsts.CPS002E, "CPS002_110",
            "/view/cps/002/e/index.xhtml");

    /**
     * CPS002F
     */
    public static final Program CPS002F = new Program(WebAppId.COM, ProgramIdConsts.CPS002F, "CPS002_6",
            "/view/cps/002/f/index.xhtml");

    /**
     * CPS002G
     */
    public static final Program CPS002G = new Program(WebAppId.COM, ProgramIdConsts.CPS002G, "CPS002_7",
            "/view/cps/002/g/index.xhtml");

    /**
     * CPS002H
     */
    public static final Program CPS002H = new Program(WebAppId.COM, ProgramIdConsts.CPS002H, "CPS002_8",
            "/view/cps/002/h/index.xhtml");

    /**
     * CPS002I
     */
    public static final Program CPS002I = new Program(WebAppId.COM, ProgramIdConsts.CPS002I, "CPS002_9",
            "/view/cps/002/i/index.xhtml");

    /**
     * CPS002J
     */
    public static final Program CPS002J = new Program(WebAppId.COM, ProgramIdConsts.CPS002J, "CPS002_111",
            "/view/cps/002/j/index.xhtml");

    public static final Program CMM008A = new Program(WebAppId.COM, ProgramIdConsts.CMM008A, null,
            "/view/cmm/008/a/index.xhtml");
    
    /**
     * CPS018
     */
    public static final Program CPS018A = new Program(WebAppId.COM, ProgramIdConsts.CPS018A, "CPS018_1",
            "/view/cps/018/a/index.xhtml");

    /**
     * The Constant CMM014.
     */
    public static final Program CMM014A = new Program(WebAppId.COM, ProgramIdConsts.CMM014A, "CMM014_9",
            "/view/cmm/014/a/index.xhtml");

    public static final Program CDL002 = new Program(WebAppId.COM, ProgramIdConsts.CDL002, "CDL002_4",
            "/view/cdl/002/a/index.xhtml");

    public static final Program CDL003 = new Program(WebAppId.COM, ProgramIdConsts.CDL003, "CDL003_4",
            "/view/cdl/003/a/index.xhtml");

    public static final Program CMM011A = new Program(WebAppId.COM, ProgramIdConsts.CMM011A, "CMM011_201",
            "/view/cmm/011/a/index.xhtml");

    public static final Program CMM011B = new Program(WebAppId.COM, ProgramIdConsts.CMM011B, "CMM011_202",
            "/view/cmm/011/b/index.xhtml");

    public static final Program CMM011C = new Program(WebAppId.COM, ProgramIdConsts.CMM011C, "CMM011_101",
            "/view/cmm/011/c/index.xhtml");

    public static final Program CMM011D = new Program(WebAppId.COM, ProgramIdConsts.CMM011D, "CMM011_203",
            "/view/cmm/011/d/index.xhtml");

//	public static final Program CMM011E = new Program(WebAppId.COM, ProgramIdConsts.CMM011E, "CMM011_4",
//			"/view/cmm/011/e/index.xhtml");
//
//	public static final Program CMM011F = new Program(WebAppId.COM, ProgramIdConsts.CMM011F, "CMM011_5",
//			"/view/cmm/011/f/index.xhtml");

    public static final Program CDL008 = new Program(WebAppId.COM, ProgramIdConsts.CDL008, "CDL008_1",
            "/view/cdl/008/a/index.xhtml");

    public static final Program CDL009 = new Program(WebAppId.COM, ProgramIdConsts.CDL009, "CDL009_1",
            "/view/cdl/009/a/index.xhtml");

    public static final Program KMK002A = new Program(WebAppId.AT, ProgramIdConsts.KMK002A, "KMK002_73",
            "/view/kmk/002/a/index.xhtml");

    public static final Program KMK002B = new Program(WebAppId.AT, ProgramIdConsts.KMK002B, "KMK002_74",
            "/view/kmk/002/b/index.xhtml");

    public static final Program KMK002C = new Program(WebAppId.AT, ProgramIdConsts.KMK002C, "KMK002_43",
            "/view/kmk/002/c/index.xhtml");

    public static final Program KMK002D = new Program(WebAppId.AT, ProgramIdConsts.KMK002D, "KMK002_42",
            "/view/kmk/002/d/index.xhtml");

    public static final Program KMK006A = new Program(WebAppId.AT, ProgramIdConsts.KMK006A, "KMK006_1",
            "/view/kmk/006/a/index.xhtml");

    public static final Program KMK006B = new Program(WebAppId.AT, ProgramIdConsts.KMK006B, "KMK006_2",
            "/view/kmk/006/b/index.xhtml");

    public static final Program KMK006C = new Program(WebAppId.AT, ProgramIdConsts.KMK006C, "KMK006_3",
            "/view/kmk/006/c/index.xhtml");

    public static final Program KMK006D = new Program(WebAppId.AT, ProgramIdConsts.KMK006D, "KMK006_4",
            "/view/kmk/006/d/index.xhtml");

    public static final Program KMK006E = new Program(WebAppId.AT, ProgramIdConsts.KMK006E, "KMK006_5",
            "/view/kmk/006/e/index.xhtml");

    public static final Program KMK010A = new Program(WebAppId.AT, ProgramIdConsts.KMK010A, "KMK010_1",
            "/view/kmk/010/a/index.xhtml");

    public static final Program KMK010B = new Program(WebAppId.AT, ProgramIdConsts.KMK010B, "KMK010_2",
            "/view/kmk/010/b/index.xhtml");

    public static final Program KMK010C = new Program(WebAppId.AT, ProgramIdConsts.KMK010C, "KMK010_3",
            "/view/kmk/010/c/index.xhtml");

    public static final Program KMK010D = new Program(WebAppId.AT, ProgramIdConsts.KMK010D, "KMK010_71",
            "/view/kmk/010/d/index.xhtml");

    public static final Program KMK015A = new Program(WebAppId.AT, ProgramIdConsts.KMK015A, "KMK015_1",
            "/view/kmk/015/a/index.xhtml");

    /**
     * The Constant KMK015B.
     */
    public static final Program KMK015B = new Program(WebAppId.AT, ProgramIdConsts.KMK015B, "KMK015_25",
            "/view/kmk/015/b/index.xhtml");

    /**
     * The Constant KMK015C.
     */
    public static final Program KMK015C = new Program(WebAppId.AT, ProgramIdConsts.KMK015C, "KMK015_26",
            "/view/kmk/015/c/index.xhtml");

    public static final Program KDL006 = new Program(WebAppId.AT, ProgramIdConsts.KDL006, "KDL006_1",
            "/view/kdl/006/a/index.xhtml");

    public static final Program KMW005A = new Program(WebAppId.AT, ProgramIdConsts.KMW005A, "KMW005_27",
            "/view/kmw/005/a/index.xhtml");

    public static final Program KMW005B = new Program(WebAppId.AT, ProgramIdConsts.KMW005B, "KMW005_28",
            "/view/kmw/005/b/index.xhtml");

    public static final Program CMM050A = new Program(WebAppId.COM, ProgramIdConsts.CMM050A, "CMM050_1",
            "/view/cmm/050/a/index.xhtml");

    public static final Program CMM050B = new Program(WebAppId.COM, ProgramIdConsts.CMM050B, "CMM050_2",
            "/view/cmm/050/b/index.xhtml");

    public static final Program CMM013A = new Program(WebAppId.COM, ProgramIdConsts.CMM013A, "CMM013_53",
            "/view/cmm/013/a/index.xhtml");

    public static final Program CMM013B = new Program(WebAppId.COM, ProgramIdConsts.CMM013B, "CMM013_54",
            "/view/cmm/013/b/index.xhtml");

    public static final Program CMM013C = new Program(WebAppId.COM, ProgramIdConsts.CMM013C, "CMM013_55",
            "/view/cmm/013/c/index.xhtml");

    public static final Program CMM013D = new Program(WebAppId.COM, ProgramIdConsts.CMM013D, "CMM013_56",
            "/view/cmm/013/d/index.xhtml");

    public static final Program CMM013E = new Program(WebAppId.COM, ProgramIdConsts.CMM013E, "CMM013_57",
            "/view/cmm/013/e/index.xhtml");

    public static final Program CMM013F = new Program(WebAppId.COM, ProgramIdConsts.CMM013F, "CMM013_58",
            "/view/cmm/013/f/index.xhtml");

    public static final Program CMM013H = new Program(WebAppId.COM, ProgramIdConsts.CMM013H, "CMM013_69",
            "/view/cmm/013/h/index.xhtml");

    public static final Program CDL004 = new Program(WebAppId.COM, ProgramIdConsts.CDL004, "CDL004_1",
            "/view/cdl/004/a/index.xhtml");

    public static final Program KDW003A = new Program(WebAppId.AT, ProgramIdConsts.KDW003A, "KDW003_55",
            "/view/kdw/003/a/index.xhtml");

    public static final Program KDW003B = new Program(WebAppId.AT, ProgramIdConsts.KDW003B, "KDW003_54",
            "/view/kdw/003/b/index.xhtml");

    public static final Program KDW003C = new Program(WebAppId.AT, ProgramIdConsts.KDW003C, "KDW003_53",
            "/view/kdw/003/c/index.xhtml");

    public static final Program KDW003D = new Program(WebAppId.AT, ProgramIdConsts.KDW003D, "KDW003_52",
            "/view/kdw/003/d/index.xhtml");
    
    public static final Program KDW003G = new Program(WebAppId.AT, ProgramIdConsts.KDW003G, "KDW003_133",
            "/view/kdw/003/g/index.xhtml");
    
    public static final Program KDW004A = new Program(WebAppId.AT, ProgramIdConsts.KDW004A, "KDW004_1",
            "/view/kdw/004/a/index.xhtml");

    public static final Program CMM018Q = new Program(WebAppId.COM, ProgramIdConsts.CMM018Q, "CMM018_114",
            "/view/cmm/018/q/index.xhtml");

    public static final Program CMM018X = new Program(WebAppId.COM, ProgramIdConsts.CMM018X, "",
            "/view/cmm/018/x/index.xhtml");

    public static final Program CMM018A = new Program(WebAppId.COM, ProgramIdConsts.CMM018A, "CMM018_1",
            "/view/cmm/018/a/index.xhtml");

    public static final Program CMM018I = new Program(WebAppId.COM, ProgramIdConsts.CMM018I, "CMM018_9",
            "/view/cmm/018/i/index.xhtml");

    public static final Program CMM018J = new Program(WebAppId.COM, ProgramIdConsts.CMM018J, "CMM018_10",
            "/view/cmm/018/j/index.xhtml");

    public static final Program CMM018K = new Program(WebAppId.COM, ProgramIdConsts.CMM018K, "CMM018_11",
            "/view/cmm/018/k/index.xhtml");

    public static final Program CMM018L = new Program(WebAppId.COM, ProgramIdConsts.CMM018L, "CMM018_12",
            "/view/cmm/018/l/index.xhtml");

    public static final Program CMM018M = new Program(WebAppId.COM, ProgramIdConsts.CMM018M, "CMM018_13",
            "/view/cmm/018/m/index.xhtml");

    public static final Program CMM018N = new Program(WebAppId.COM, ProgramIdConsts.CMM018N, "CMM018_14",
            "/view/cmm/018/n/index.xhtml");

    public static final Program KDL032 = new Program(WebAppId.AT, ProgramIdConsts.KDL032A, "KDL032_1",
            "/view/kdl/032/a/index.xhtml");

    public static final Program CPS009A = new Program(WebAppId.COM, ProgramIdConsts.CPS009A, "CPS009_1",
            "/view/cps/009/a/index.xhtml");

    public static final Program CPS009B = new Program(WebAppId.COM, ProgramIdConsts.CPS009B, "CPS009_9",
            "/view/cps/009/b/index.xhtml");

    public static final Program CPS009C = new Program(WebAppId.COM, ProgramIdConsts.CPS009C, "CPS009_2",
            "/view/cps/009/c/index.xhtml");

    public static final Program CPS009D = new Program(WebAppId.COM, ProgramIdConsts.CPS009D, "CPS009_36",
            "/view/cps/009/d/index.xhtml");

    public static final Program CPS017A = new Program(WebAppId.COM, ProgramIdConsts.CPS017A, "CPS017_51",
            "/view/cps/017/a/index.xhtml");

    public static final Program CPS017B = new Program(WebAppId.COM, ProgramIdConsts.CPS017B, "CPS017_52",
            "/view/cps/017/b/index.xhtml");

    public static final Program CPS017C = new Program(WebAppId.COM, ProgramIdConsts.CPS017C, "CPS017_53",
            "/view/cps/017/c/index.xhtml");

    public static final Program CPS017D = new Program(WebAppId.COM, ProgramIdConsts.CPS017D, "CPS017_54",
            "/view/cps/017/d/index.xhtml");

    public static final Program CAS005A = new Program(WebAppId.COM, ProgramIdConsts.CAS005A, "CAS005_1",
            "/view/cas/005/a/index.xhtml");

    public static final Program CAS005B = new Program(WebAppId.COM, ProgramIdConsts.CAS005B, "CAS005_2",
            "/view/cas/005/b/index.xhtml");

    public static final Program CAS005C = new Program(WebAppId.COM, ProgramIdConsts.CAS005C, "CAS005_3",
            "/view/cas/005/c/index.xhtml");

    public static final Program CAS009A = new Program(WebAppId.COM, ProgramIdConsts.CAS009A, "CAS009_1",
            "/view/cas/009/a/index.xhtml");

    public static final Program CAS009B = new Program(WebAppId.COM, ProgramIdConsts.CAS009B, "CAS009_2",
            "/view/cas/009/b/index.xhtml");

    public static final Program CAS011A = new Program(WebAppId.COM, ProgramIdConsts.CAS011A, "CAS011_1",
            "/view/cas/011/a/index.xhtml");

    public static final Program CAS011C = new Program(WebAppId.COM, ProgramIdConsts.CAS011C, "CAS011_3",
            "/view/cas/011/c/index.xhtml");

    public static final Program CAS012A = new Program(WebAppId.COM, ProgramIdConsts.CAS012A, "CAS012_1",
            "/view/cas/012/a/index.xhtml");

    public static final Program CAS012B = new Program(WebAppId.COM, ProgramIdConsts.CAS012B, "CAS012_2",
            "/view/cas/012/b/index.xhtml");

    public static final Program CAS012C = new Program(WebAppId.COM, ProgramIdConsts.CAS012C, "CAS012_3",
            "/view/cas/012/c/index.xhtml");

    public static final Program CAS013A = new Program(WebAppId.COM, ProgramIdConsts.CAS013A, "CAS013_1",
            "/view/cas/013/a/index.xhtml");

    public static final Program CAS013B = new Program(WebAppId.COM, ProgramIdConsts.CAS013B, "CAS013_2",
            "/view/cas/013/b/index.xhtml");

    public static final Program CAS014A = new Program(WebAppId.COM, ProgramIdConsts.CAS014A, "CAS014_2",
            "/view/cas/014/a/index.xhtml");

    public static final Program CAS014B = new Program(WebAppId.COM, ProgramIdConsts.CAS014B, "CAS014_3",
            "/view/cas/014/b/index.xhtml");

    public static final Program CCG025 = new Program(WebAppId.COM, ProgramIdConsts.CCG025, "CCG025_1",
            "/view/ccg/025/component/index.xhtml");

    public static final Program CDL025 = new Program(WebAppId.COM, ProgramIdConsts.CDL025, "CDL025_1",
            "/view/cdl/025/index.xhtml");

    public static final Program CMM007A = new Program(WebAppId.COM, ProgramIdConsts.CMM007A, "CMM007_1",
            "/view/cmm/007/a/index.xhtml");

    public static final Program CMM007B = new Program(WebAppId.COM, ProgramIdConsts.CMM007B, "CMM007_1",
            "/view/cmm/007/b/index.xhtml");

    public static final Program CMM007C = new Program(WebAppId.COM, ProgramIdConsts.CMM007C, "CMM007_1",
            "/view/cmm/007/c/index.xhtml");

    public static final Program CMM007D = new Program(WebAppId.COM, ProgramIdConsts.CMM007D, "CMM007_1",
            "/view/cmm/007/d/index.xhtml");

    public static final Program CMM007E = new Program(WebAppId.COM, ProgramIdConsts.CMM007E, "CMM007_1",
            "/view/cmm/007/e/index.xhtml");

    public static final Program CMM007G = new Program(WebAppId.COM, ProgramIdConsts.CMM007G, "CMM007_1",
            "/view/cmm/007/g/index.xhtml");

    public static final Program CCG026 = new Program(WebAppId.COM, ProgramIdConsts.CCG026, "CCG025_2",
            "/view/ccg/026/component/index.xhtml");

    public static final Program CMM021A = new Program(WebAppId.COM, ProgramIdConsts.CMM021A, "CMM021_1",
            "/view/cmm/021/a/index.xhtml");

    public static final Program CMM021B = new Program(WebAppId.COM, ProgramIdConsts.CMM021B, "CMM021_1",
            "/view/cmm/021/b/index.xhtml");

    public static final Program CMM021C = new Program(WebAppId.COM, ProgramIdConsts.CMM021C, "CMM021_1",
            "/view/cmm/021/c/index.xhtml");

    public static final Program CMM023A = new Program(WebAppId.COM, ProgramIdConsts.CMM023A, "CMM023_A",
            "/view/cmm/023/a/index.xhtml");

    public static final Program CDL023A = new Program(WebAppId.COM, ProgramIdConsts.CDL023A, "CDL023_1",
            "/view/cdl/023/a/index.xhtml");

    public static final Program KMF002A = new Program(WebAppId.AT, ProgramIdConsts.KMF002A, "KMF002_89",
            "/view/kmf/002/a/index.xhtml");

    public static final Program KMF002B = new Program(WebAppId.AT, ProgramIdConsts.KMF002B, "KMF002_1",
            "/view/kmf/002/b/index.xhtml");

    public static final Program KMF002C = new Program(WebAppId.AT, ProgramIdConsts.KMF002C, "KMF002_1",
            "/view/kmf/002/c/index.xhtml");

    public static final Program KMF002D = new Program(WebAppId.AT, ProgramIdConsts.KMF002D, "KMF002_1",
            "/view/kmf/002/d/index.xhtml");

    public static final Program KMF002E = new Program(WebAppId.AT, ProgramIdConsts.KMF002E, "KMF002_1",
            "/view/kmf/002/e/index.xhtml");

    public static final Program KMF002F = new Program(WebAppId.AT, ProgramIdConsts.KMF002F, "KMF002_26",
            "/view/kmf/002/f/index.xhtml");
    
    public static final Program KMF002G = new Program(WebAppId.AT, ProgramIdConsts.KMF002G, "KMF002_94",
            "/view/kmf/002/g/index.xhtml");
    
    public static final Program KMF002H = new Program(WebAppId.AT, ProgramIdConsts.KMF002H, "KMF002_116",
            "/view/kmf/002/h/index.xhtml");

    public static final Program KMF002M = new Program(WebAppId.AT, ProgramIdConsts.KMF002M, "KMF002_95",
            "/view/kmf/002/m/index.xhtml");
    
    public static final Program KMK003A = new Program(WebAppId.AT, ProgramIdConsts.KMK003A, "KMK003_1",
            "/view/kmk/003/a/index.xhtml");
    public static final Program KMK003B = new Program(WebAppId.AT, ProgramIdConsts.KMK003B, "KMK003_1",
            "/view/kmk/003/b/index.xhtml");
    public static final Program KMK003C = new Program(WebAppId.AT, ProgramIdConsts.KMK003C, "KMK003_1",
            "/view/kmk/003/c/index.xhtml");
    public static final Program KMK003D = new Program(WebAppId.AT, ProgramIdConsts.KMK003D, "KMK003_1",
            "/view/kmk/003/d/index.xhtml");
    public static final Program KMK003E = new Program(WebAppId.AT, ProgramIdConsts.KMK003E, null,
            "/view/kmk/003/e/index.xhtml");
    public static final Program KMK003F = new Program(WebAppId.AT, ProgramIdConsts.KMK003F, "KMK003_287",
            "/view/kmk/003/f/index.xhtml");
    public static final Program KMK003G = new Program(WebAppId.AT, ProgramIdConsts.KMK003G, "KMK003_288",
            "/view/kmk/003/g/index.xhtml");
    public static final Program KMK003G2 = new Program(WebAppId.AT, ProgramIdConsts.KMK003G2, "KMK003_288",
            "/view/kmk/003/g/index2.xhtml");
    public static final Program KMK003H = new Program(WebAppId.AT, ProgramIdConsts.KMK003H, "KMK003_289",
            "/view/kmk/003/h/index.xhtml");
    public static final Program KMK003H2 = new Program(WebAppId.AT, ProgramIdConsts.KMK003H2, "KMK003_290",
            "/view/kmk/003/h/index2.xhtml");
    public static final Program KMK003I = new Program(WebAppId.AT, ProgramIdConsts.KMK003I, "KMK003_291",
            "/view/kmk/003/i/index.xhtml");
    public static final Program KMK003J = new Program(WebAppId.AT, ProgramIdConsts.KMK003J, "KMK003_292",
            "/view/kmk/003/j/index.xhtml");
    public static final Program KMK003K = new Program(WebAppId.AT, ProgramIdConsts.KMK003K, null,
            "/view/kmk/003/k/index.xhtml");
    public static final Program KMK003L = new Program(WebAppId.AT, ProgramIdConsts.KMK003L, null,
            "/view/kmk/003/l/index.xhtml");

    public static final Program KSU007A = new Program(WebAppId.AT, ProgramIdConsts.KSU007A, "KSU007_8",
            "/view/ksu/007/a/index.xhtml");
    public static final Program KSU007B = new Program(WebAppId.AT, ProgramIdConsts.KSU007B, "KSU007_23",
            "/view/ksu/007/b/index.xhtml");

    public static final Program CPS016A = new Program(WebAppId.COM, ProgramIdConsts.CPS016A, "CPS016_26",
            "/view/cps/016/a/index.xhtml");

    public static final Program KMK013A = new Program(WebAppId.AT, ProgramIdConsts.KMK013A, "KMK013_236",
            "/view/kmk/013/a/index.xhtml");
    public static final Program KMK013B = new Program(WebAppId.AT, ProgramIdConsts.KMK013B, "KMK013_237",
            "/view/kmk/013/b/index.xhtml");
    public static final Program KMK013C = new Program(WebAppId.AT, ProgramIdConsts.KMK013C, "KMK013_238",
            "/view/kmk/013/c/index.xhtml");
    public static final Program KMK013D = new Program(WebAppId.AT, ProgramIdConsts.KMK013D, "KMK013_239",
            "/view/kmk/013/d/index.xhtml");
    public static final Program KMK013E = new Program(WebAppId.AT, ProgramIdConsts.KMK013E, "KMK013_240",
            "/view/kmk/013/e/index.xhtml");
    public static final Program KMK013F = new Program(WebAppId.AT, ProgramIdConsts.KMK013F, null,
            "/view/kmk/013/f/index.xhtml");
    public static final Program KMK013G = new Program(WebAppId.AT, ProgramIdConsts.KMK013G, "KMK013_419",
            "/view/kmk/013/g/index.xhtml");
    public static final Program KMK013H = new Program(WebAppId.AT, ProgramIdConsts.KMK013H, "KMK013_241",
            "/view/kmk/013/h/index.xhtml");
    public static final Program KMK013I = new Program(WebAppId.AT, ProgramIdConsts.KMK013I, "KMK013_432",
            "/view/kmk/013/i/index.xhtml");
    public static final Program KMK013J = new Program(WebAppId.AT, ProgramIdConsts.KMK013J, "KMK013_439",
            "/view/kmk/013/j/index.xhtml");
    public static final Program KMK013K = new Program(WebAppId.AT, ProgramIdConsts.KMK013K, "KMK013_420",
            "/view/kmk/013/k/index.xhtml");
    public static final Program KMK013L = new Program(WebAppId.AT, ProgramIdConsts.KMK013L, "KMK013_430",
            "/view/kmk/013/l/index.xhtml");
    public static final Program KMK013M = new Program(WebAppId.AT, ProgramIdConsts.KMK013M, "KMK013_437",
            "/view/kmk/013/m/index.xhtml");
    public static final Program KMK013N = new Program(WebAppId.AT, ProgramIdConsts.KMK013N, "KMK013_428",
            "/view/kmk/013/n/index.xhtml");
    public static final Program KMK013O = new Program(WebAppId.AT, ProgramIdConsts.KMK013O, "KMK013_441",
            "/view/kmk/013/o/index.xhtml");
    public static final Program KMK013P = new Program(WebAppId.AT, ProgramIdConsts.KMK013P, "KMK013_442",
            "/view/kmk/013/p/index.xhtml");
    public static final Program KMK013Q = new Program(WebAppId.AT, ProgramIdConsts.KMK013Q, "KMK013_421",
            "/view/kmk/013/q/index.xhtml");
    public static final Program KMK013R = new Program(WebAppId.AT, ProgramIdConsts.KMK013R, "KMK013_518",
            "/view/kmk/013/r/index.xhtml");

    /** KAF020 */
    public static final Program KAF020A = new Program(WebAppId.AT, ProgramIdConsts.KAF020A, "KAF020_1",
            "/view/kaf/020/a/index.xhtml");
    public static final Program KAF020B = new Program(WebAppId.AT, ProgramIdConsts.KAF020B, "KAF020_2",
            "/view/kaf/020/b/index.xhtml");

    /** KAF021 */
    /**
     * KAF021
     */
    public static final Program KAF021A = new Program(WebAppId.AT, ProgramIdConsts.KAF021A, "KAF021_60",
            "/view/kaf/021/a/index.xhtml");
    public static final Program KAF021B = new Program(WebAppId.AT, ProgramIdConsts.KAF021B, "KAF021_61",
            "/view/kaf/021/b/index.xhtml");
    public static final Program KAF021C = new Program(WebAppId.AT, ProgramIdConsts.KAF021C, "KAF021_62",
            "/view/kaf/021/c/index.xhtml");
    public static final Program KAF021D = new Program(WebAppId.AT, ProgramIdConsts.KAF021D, "KAF021_63",
            "/view/kaf/021/d/index.xhtml");

    /**
     * KAF022
     */
    public static final Program KAF022A = new Program(WebAppId.AT, ProgramIdConsts.KAF022A, "KAF022_768",
            "/view/kaf/022/a/index.xhtml");
    public static final Program KAF022B = new Program(WebAppId.AT, ProgramIdConsts.KAF022B, "KAF022_769",
            "/view/kaf/022/b/index.xhtml");
    public static final Program KAF022C = new Program(WebAppId.AT, ProgramIdConsts.KAF022C, "KAF022_770",
            "/view/kaf/022/c/index.xhtml");
    public static final Program KAF022D = new Program(WebAppId.AT, ProgramIdConsts.KAF022D, "KAF022_771",
            "/view/kaf/022/d/index.xhtml");
    public static final Program KAF022S = new Program(WebAppId.AT, ProgramIdConsts.KAF022S, "KAF022_444",
            "/view/kaf/022/s/index.xhtml");
    public static final Program KAF022O = new Program(WebAppId.AT, ProgramIdConsts.KAF022O, "KAF022_708",
            "/view/kaf/022/o/index.xhtml");
    public static final Program KAF022P = new Program(WebAppId.AT, ProgramIdConsts.KAF022P, "KAF022_772",
            "/view/kaf/022/p/index.xhtml");

    /* KSM011 */
    public static final Program KSM011A = new Program(WebAppId.AT, ProgramIdConsts.KSM011A, null,
            "/view/ksm/011/a/index.xhtml");
    public static final Program KSM011B = new Program(WebAppId.AT, ProgramIdConsts.KSM011B, null,
            "/view/ksm/011/b/index.xhtml");
    public static final Program KSM011C = new Program(WebAppId.AT, ProgramIdConsts.KSM011C, null,
            "/view/ksm/011/c/index.xhtml");
    public static final Program KSM011D = new Program(WebAppId.AT, ProgramIdConsts.KSM011D, "KSM011_4",
            "/view/ksm/011/d/index.xhtml");
    public static final Program KSM011E = new Program(WebAppId.AT, ProgramIdConsts.KSM011E, "KSM011_44",
            "/view/ksm/011/e/index.xhtml");
    public static final Program KSM011F = new Program(WebAppId.AT, ProgramIdConsts.KSM011F, "KSM011_109",
            "/view/ksm/011/f/index.xhtml");

    /**
     * KSC001A
     */
    public static final Program KSC001A = new Program(WebAppId.AT, ProgramIdConsts.KSC001A, null,
            "/view/ksc/001/a/index.xhtml");
    /**
     * KSC001B
     */
    public static final Program KSC001B = new Program(WebAppId.AT, ProgramIdConsts.KSC001B, null,
            "/view/ksc/001/b/index.xhtml");
    /**
     * KSC001C
     */
    public static final Program KSC001C = new Program(WebAppId.AT, ProgramIdConsts.KSC001C, null,
            "/view/ksc/001/c/index.xhtml");
    /**
     * KSC001D
     */
    public static final Program KSC001D = new Program(WebAppId.AT, ProgramIdConsts.KSC001D, null,
            "/view/ksc/001/d/index.xhtml");
    /**
     * KSC001E
     */
    public static final Program KSC001E = new Program(WebAppId.AT, ProgramIdConsts.KSC001E, null,
            "/view/ksc/001/e/index.xhtml");
    /**
     * KSC001F
     */
    public static final Program KSC001F = new Program(WebAppId.AT, ProgramIdConsts.KSC001F, "KSC001_3",
            "/view/ksc/001/f/index.xhtml");
    /**
     * KSC001G
     */
    public static final Program KSC001G = new Program(WebAppId.AT, ProgramIdConsts.KSC001G, null,
            "/view/ksc/001/g/index.xhtml");
    /**
     * KSC001H
     */
    public static final Program KSC001H = new Program(WebAppId.AT, ProgramIdConsts.KSC001H, "KSC001_70",
            "/view/ksc/001/h/index.xhtml");
    /**
     * KSC001I
     */
    public static final Program KSC001I = new Program(WebAppId.AT, ProgramIdConsts.KSC001I, "KSC001_79",
            "/view/ksc/001/i/index.xhtml");
    /**
     * KSC001K
     */
    public static final Program KSC001K = new Program(WebAppId.AT, ProgramIdConsts.KSC001K, "KSC001_80",
            "/view/ksc/001/k/index.xhtml");
    /**
     * KAF000
     */
    public static final Program KAF000B = new Program(WebAppId.AT, ProgramIdConsts.KAF000B, "",
            "/view/kaf/000/b/index.xhtml", ProgramIdConsts.CCGS33);
    public static final Program KAF000D = new Program(WebAppId.AT, ProgramIdConsts.KAF000D, "KAF000_37",
            "/view/kaf/000/d/index.xhtml", ProgramIdConsts.KAF000D);
    public static final Program KAF000E = new Program(WebAppId.AT, ProgramIdConsts.KAF000E, "KAF000_64",
            "/view/kaf/000/e/index.xhtml", ProgramIdConsts.KAF000E);
    public static final Program KAF000F = new Program(WebAppId.AT, ProgramIdConsts.KAF000F, "KAF000_65",
            "/view/kaf/000/f/index.xhtml", ProgramIdConsts.KAF000F);
    /**
     * KAF001
     */
    public static final Program KAF001A = new Program(WebAppId.AT, ProgramIdConsts.KAF001A, "KAF001_1",
            "/view/kaf/001/a/index.xhtml");
    public static final Program KAF001B = new Program(WebAppId.AT, ProgramIdConsts.KAF001B, "KAF001_2",
            "/view/kaf/001/b/index.xhtml");
    /**
     * KAF002A
     */
    public static final Program KAF002A = new Program(WebAppId.AT, ProgramIdConsts.KAF002A, null,
            "/view/kaf/002/a/index.xhtml");
    public static final Program KAF002B = new Program(WebAppId.AT, ProgramIdConsts.KAF002B, null,
            "/view/kaf/002/b/index.xhtml");
    public static final Program KAF002C = new Program(WebAppId.AT, ProgramIdConsts.KAF002C, null,
            "/view/kaf/002/c/index.xhtml");
    /**
     * KAF004A
     */
    public static final Program KAF004A = new Program(WebAppId.AT, ProgramIdConsts.KAF004A, null,
            "/view/kaf/004/a/index.xhtml");
    /**
     * KAF005A
     */
    public static final Program KAF004B = new Program(WebAppId.AT, ProgramIdConsts.KAF004B, null,
            "/view/kaf/004/b/index.xhtml");
    /**
     * KAF005A
     */
    public static final Program KAF005A = new Program(WebAppId.AT, ProgramIdConsts.KAF005A, "KAF005_1",
            "/view/kaf/005/a/index.xhtml");
    /**
     * KAF006A
     */
    public static final Program KAF006A = new Program(WebAppId.AT, ProgramIdConsts.KAF006A, "KAF006_66",
            "/view/kaf/006/a/index.xhtml");
    /**
     * KAF006C
     */
    public static final Program KAF006C = new Program(WebAppId.AT, ProgramIdConsts.KAF006C, "KAF006_96",
            "/view/kaf/006/c/index.xhtml");
    /**
     * KAF007A
     */
    public static final Program KAF007A = new Program(WebAppId.AT, ProgramIdConsts.KAF007A, null,
            "/view/kaf/007/a/index.xhtml");
    /**
     * KAF008A
     */
    public static final Program KAF008A = new Program(WebAppId.AT, ProgramIdConsts.KAF008A, "KAF008_1",
            "/view/kaf/008/a/index.xhtml");
    /**
     * KAF007A
     */
    public static final Program KAF009A = new Program(WebAppId.AT, ProgramIdConsts.KAF009A, null,
            "/view/kaf/009/a/index.xhtml");
    /**
     * KAF010A
     */
    public static final Program KAF010A = new Program(WebAppId.AT, ProgramIdConsts.KAF010A, "KAF010_1",
            "/view/kaf/010/a/index.xhtml");
    /**
     * KAF011A
     */
    public static final Program KAF011A = new Program(WebAppId.AT, ProgramIdConsts.KAF011A, "KAF011_80",
            "/view/kaf/011/a/index.xhtml");
    /**
     * KAF011B
     */
    public static final Program KAF011B = new Program(WebAppId.AT, ProgramIdConsts.KAF011B, "KAF011_81",
            "/view/kaf/011/b/index.xhtml");
    /**
     * KAF011C
     */
    public static final Program KAF011C = new Program(WebAppId.AT, ProgramIdConsts.KAF011C, "KAF011_62",
            "/view/kaf/011/c/index.xhtml");

    public static final Program QMM012A = new Program(WebAppId.PR, ProgramIdConsts.QMM012A, "QMM012_1",
            "/view/qmm/012/a/index.xhtml");
    public static final Program QMM012B = new Program(WebAppId.PR, ProgramIdConsts.QMM012B, "QMM012_2",
            "/view/qmm/012/b/index.xhtml");
    public static final Program QMM012H = new Program(WebAppId.PR, ProgramIdConsts.QMM012H, "QMM012_8",
            "/view/qmm/012/h/index.xhtml");
    public static final Program QMM012I = new Program(WebAppId.PR, ProgramIdConsts.QMM012I, "QMM012_9",
            "/view/qmm/012/i/index.xhtml");
    public static final Program QMM012J = new Program(WebAppId.PR, ProgramIdConsts.QMM012J, "QMM012_10",
            "/view/qmm/012/j/index.xhtml");
    public static final Program QMM012K = new Program(WebAppId.PR, ProgramIdConsts.QMM012K, "QMM012_11",
            "/view/qmm/012/k/index.xhtml");

    public static final Program QMM013A = new Program(WebAppId.PR, ProgramIdConsts.QMM013A, "QMM013_39",
            "/view/qmm/013/a/index.xhtml");
    // TODO: Define new programs here.
    /**
     * CMM051A
     */
    public static final Program CMM051A = new Program(WebAppId.COM, ProgramIdConsts.CMM051A, "CMM051_35",
            "/view/cmm/051/a/index.xhtml");
    public static final Program CMM051B = new Program(WebAppId.COM, ProgramIdConsts.CMM051B, "CMM051_36",
            "/view/cmm/051/b/index.xhtml");
    public static final Program CMM051C = new Program(WebAppId.COM, ProgramIdConsts.CMM051C, "CMM051_37",
            "/view/cmm/051/c/index.xhtml");
    /**
     * CMM052A
     */
    public static final Program CMM052A = new Program(WebAppId.COM, ProgramIdConsts.CMM052A, "CMM052_1",
            "/view/cmm/052/a/index.xhtml");
    /**
     * CMM001
     */
    public static final Program CMM001A = new Program(WebAppId.COM, ProgramIdConsts.CMM001A, "CMM001_39",
            "/view/cmm/001/a/index.xhtml");
    public static final Program CMM001B = new Program(WebAppId.COM, ProgramIdConsts.CMM001B, null,
            "/view/cmm/001/b/index.xhtml");
    public static final Program CMM001C = new Program(WebAppId.COM, ProgramIdConsts.CMM001C, null,
            "/view/cmm/001/c/index.xhtml");
    public static final Program CMM001D = new Program(WebAppId.COM, ProgramIdConsts.CMM001D, null,
            "/view/cmm/001/d/index.xhtml");
    /**
     * CMM001F
     */
    public static final Program CMM001F = new Program(WebAppId.COM, ProgramIdConsts.CMM001F, "CMM001_69",
            "/view/cmm/001/f/index.xhtml");

    public static final Program CMM002A = new Program(WebAppId.COM, ProgramIdConsts.CMM002A, "CMM002_11",
            "/view/cmm/002/a/index.xhtml");

    /**
     * CMM020A
     */
    public static final Program CMM020A = new Program(WebAppId.COM, ProgramIdConsts.CMM020A, "CMM020_9",
            "/view/cmm/020/a/index.xhtml");
    /** CCG018 */
    /**
     * CCG022
     */
    public static final Program CCG022A = new Program(WebAppId.COM, ProgramIdConsts.CCG022A, "CCG022_1",
            "/view/ccg/022/a/index.xhtml");
    /**
     * KAL001
     */
    public static final Program KAL001A = new Program(WebAppId.AT, ProgramIdConsts.KAL001A, "KAL001_1",
            "/view/kal/001/a/index.xhtml");
    public static final Program KAL001B = new Program(WebAppId.AT, ProgramIdConsts.KAL001B, "KAL001_1",
            "/view/kal/001/b/index.xhtml");
    public static final Program KAL001C = new Program(WebAppId.AT, ProgramIdConsts.KAL001C, "KAL001_39",
            "/view/kal/001/c/index.xhtml");
    public static final Program KAL001D = new Program(WebAppId.AT, ProgramIdConsts.KAL001D, "KAL001_38",
            "/view/kal/001/d/index.xhtml");
    /**
     * KAL002
     */
    public static final Program KAL002A = new Program(WebAppId.AT, ProgramIdConsts.KAL002A, "KAL002_1",
            "/view/kal/002/a/index.xhtml");
    public static final Program KAL002B = new Program(WebAppId.AT, ProgramIdConsts.KAL002B, "KAL002_12",
            "/view/kal/002/b/index.xhtml");
    /**
     * KAL003
     */
    public static final Program KAL003A = new Program(WebAppId.AT, ProgramIdConsts.KAL003A, "KAL003_1",
            "/view/kal/003/a/index.xhtml");
    public static final Program KAL003B = new Program(WebAppId.AT, ProgramIdConsts.KAL003B, "KAL003_2",
            "/view/kal/003/b/index.xhtml");
    public static final Program KAL003C = new Program(WebAppId.AT, ProgramIdConsts.KAL003C, "KAL003_3",
            "/view/kal/003/c/index.xhtml");
    public static final Program KAL003C2 = new Program(WebAppId.AT, ProgramIdConsts.KAL003C2, "KAL003_3",
            "/view/kal/003/c1/index.xhtml");
    public static final Program KAL003D = new Program(WebAppId.AT, ProgramIdConsts.KAL003D, "KAL003_4",
            "/view/kal/003/d/index.xhtml");
    
    /**
     * KAL004
     */
    public static final Program KAL004A = new Program(WebAppId.AT, ProgramIdConsts.KAL004A, "KAL004_1",
            "/view/kal/004/a/index.xhtml");
    public static final Program KAL004B = new Program(WebAppId.AT, ProgramIdConsts.KAL004B, "KAL004_2",
            "/view/kal/004/b/index.xhtml");
    public static final Program KAL004D = new Program(WebAppId.AT, ProgramIdConsts.KAL004D, "KAL004_2",
            "/view/kal/004/d/index.xhtml");
    public static final Program KAL004F = new Program(WebAppId.AT, ProgramIdConsts.KAL004F, "KAL004_2",
            "/view/kal/004/f/index.xhtml");
    public static final Program KAL004G = new Program(WebAppId.AT, ProgramIdConsts.KAL004G, "KAL004_2",
            "/view/kal/004/g/index.xhtml");
	public static final Program KAL004E = new Program(WebAppId.AT, ProgramIdConsts.KAL004E, "KAL004_2",
            "/view/kal/004/e/index.xhtml");

    /**
     * KAL011
     */
    public static final Program KAL011A = new Program(WebAppId.AT, ProgramIdConsts.KAL011A, "KAL011_1",
            "/view/kal/011/a/index.xhtml");
    public static final Program KAL011B = new Program(WebAppId.AT, ProgramIdConsts.KAL011B, "KAL011_2",
            "/view/kal/011/b/index.xhtml");
    public static final Program KAL011C = new Program(WebAppId.AT, ProgramIdConsts.KAL011C, "KAL011_3",
            "/view/kal/011/c/index.xhtml");
    public static final Program KAL011D = new Program(WebAppId.AT, ProgramIdConsts.KAL011D, "KAL011_4",
            "/view/kal/011/d/index.xhtml");

    // KAL012A
    public static final Program KAL012A = new Program(WebAppId.AT, ProgramIdConsts.KAL012A, "KAL012_1",
            "/view/kal/012/a/index.xhtml");
    // KAL012A
    public static final Program KAL012B = new Program(WebAppId.AT, ProgramIdConsts.KAL012B, "KAL012_4",
            "/view/kal/012/b/index.xhtml");

    // KAL013A
    public static final Program KAL013A = new Program(WebAppId.AT, ProgramIdConsts.KAL013A, "KAL013_1",
            "/view/kal/013/a/index.xhtml");

    // KAL013B
    public static final Program KAL013B = new Program(WebAppId.AT, ProgramIdConsts.KAL013B, "KAL013_3",
            "/view/kal/013/b/index.xhtml");

    // KAL013D
    public static final Program KAL013D = new Program(WebAppId.AT, ProgramIdConsts.KAL013D, "KAL013_2",
            "/view/kal/013/d/index.xhtml");

    /**
     * KAL014A
     */
    public static final Program KAL014A = new Program(WebAppId.AT, ProgramIdConsts.KAL014A, "KAL014_1",
            "/view/kal/014/a/index.xhtml");

    /**
     * KAL014B
     */
    public static final Program KAL014B = new Program(WebAppId.AT, ProgramIdConsts.KAL014B, "KAL014_2",
            "/view/kal/014/b/index.xhtml");

    /**
     * KAL014C
     */
    public static final Program KAL014C = new Program(WebAppId.AT, ProgramIdConsts.KAL014C, "KAL014_3",
            "/view/kal/014/c/index.xhtml");

    /**
     * CMM045
     */
    public static final Program CMM045A = new Program(WebAppId.AT, ProgramIdConsts.CMM045A, "CMM045_1",
            "/view/cmm/045/a/index.xhtml");
    public static final Program CMM045B = new Program(WebAppId.AT, ProgramIdConsts.CMM045B, "CMM045_2",
            "/view/cmm/045/b/index.xhtml");

    public static final Program CPS001G = new Program(WebAppId.COM, ProgramIdConsts.CPS001G, "CPS001_97",
            "/view/cps/001/g/index.xhtml");

    /**
     * KTG001
     **/
    public static final Program KTG001A = new Program(WebAppId.AT, ProgramIdConsts.KTG001A, "KTG001_1",
            "/view/ktg/001/a/index.xhtml");
    public static final Program KTG001B = new Program(WebAppId.AT, ProgramIdConsts.KTG001B, "KTG004_24",
            "/view/ktg/001/b/index.xhtml");
    /**     * KTG002     **/
    public static final Program KTG002A = new Program(WebAppId.AT, ProgramIdConsts.KTG002A, "KTG002_1",
            "/view/ktg/002/a/index.xhtml");

    /**     * KTG004     **/
    public static final Program KTG004A = new Program(WebAppId.AT, ProgramIdConsts.KTG004A, "",
            "/view/ktg/004/a/index.xhtml");
    /**     * KTG004     **/
    public static final Program KTG004B = new Program(WebAppId.AT, ProgramIdConsts.KTG004B, "KTG004_24",
            "/view/ktg/004/b/index.xhtml");
    
    /**     * KTG005     **/
    public static final Program KTG005A = new Program(WebAppId.AT, ProgramIdConsts.KTG005A, "KTG005_14",
            "/view/ktg/005/a/index.xhtml");

    public static final Program KTG005B = new Program(WebAppId.AT, ProgramIdConsts.KTG005B, "KTG004_24",
            "/view/ktg/005/b/index.xhtml");

    /**     * KTG028     **/
    public static final Program KTG028A = new Program(WebAppId.AT, ProgramIdConsts.KTG028A, "KTG028_1",
            "/view/ktg/028/a/index.xhtml");
    /**
     * KTG029
     **/
    public static final Program KTG029A = new Program(WebAppId.AT, ProgramIdConsts.KTG029A, "KTG029_1",
            "/view/ktg/029/a/index.xhtml");

    /**
     * CMF001 外部受入
     */
    public static final Program CMF001A = new Program(WebAppId.COM, ProgramIdConsts.CMF001A, "",
            "/view/cmf/001/a/index.xhtml");
    public static final Program CMF001B = new Program(WebAppId.COM, ProgramIdConsts.CMF001B, "",
            "/view/cmf/001/b/index.xhtml");
    public static final Program CMF001C = new Program(WebAppId.COM, ProgramIdConsts.CMF001C, "",
            "/view/cmf/001/c/index.xhtml");
    public static final Program CMF001E = new Program(WebAppId.COM, ProgramIdConsts.CMF001E, "",
            "/view/cmf/001/e/index.xhtml");
    public static final Program CMF001F = new Program(WebAppId.COM, ProgramIdConsts.CMF001F, "",
            "/view/cmf/001/f/index.xhtml");
    public static final Program CMF001X = new Program(WebAppId.COM, ProgramIdConsts.CMF001X, "",
            "/view/cmf/001/x/index.xhtml");

    /**
     * CMF002
     */
    public static final Program CMF002A = new Program(WebAppId.COM, ProgramIdConsts.CMF002A, "CMF002_1",
            "/view/cmf/002/a/index.xhtml");
    public static final Program CMF002B = new Program(WebAppId.COM, ProgramIdConsts.CMF002B, "CMF002_2",
            "/view/cmf/002/b/index.xhtml");
    public static final Program CMF002C = new Program(WebAppId.COM, ProgramIdConsts.CMF002C, "CMF002_3",
            "/view/cmf/002/c/index.xhtml");
    public static final Program CMF002D = new Program(WebAppId.COM, ProgramIdConsts.CMF002D, "CMF002_4",
            "/view/cmf/002/d/index.xhtml");
    public static final Program CMF002F = new Program(WebAppId.COM, ProgramIdConsts.CMF002F, "CMF002_5",
            "/view/cmf/002/f/index.xhtml");
    public static final Program CMF002G = new Program(WebAppId.COM, ProgramIdConsts.CMF002G, "CMF002_6",
            "/view/cmf/002/g/index.xhtml");
    public static final Program CMF002H = new Program(WebAppId.COM, ProgramIdConsts.CMF002H, "CMF002_7",
            "/view/cmf/002/h/index.xhtml");
    public static final Program CMF002I = new Program(WebAppId.COM, ProgramIdConsts.CMF002I, "CMF002_8",
            "/view/cmf/002/i/index.xhtml");
    public static final Program CMF002J = new Program(WebAppId.COM, ProgramIdConsts.CMF002J, "CMF002_9",
            "/view/cmf/002/j/index.xhtml");
    public static final Program CMF002K = new Program(WebAppId.COM, ProgramIdConsts.CMF002K, "CMF002_10",
            "/view/cmf/002/k/index.xhtml");
    public static final Program CMF002L = new Program(WebAppId.COM, ProgramIdConsts.CMF002L, "CMF002_11",
            "/view/cmf/002/l/index.xhtml");
    public static final Program CMF002M = new Program(WebAppId.COM, ProgramIdConsts.CMF002M, "CMF002_12",
            "/view/cmf/002/m/index.xhtml");
    public static final Program CMF002N = new Program(WebAppId.COM, ProgramIdConsts.CMF002N, "CMF002_13",
            "/view/cmf/002/n/index.xhtml");
    public static final Program CMF002O = new Program(WebAppId.COM, ProgramIdConsts.CMF002O, "CMF002_14",
            "/view/cmf/002/o/index.xhtml");
    public static final Program CMF002P = new Program(WebAppId.COM, ProgramIdConsts.CMF002P, "CMF002_15",
            "/view/cmf/002/p/index.xhtml");
    public static final Program CMF002Q = new Program(WebAppId.COM, ProgramIdConsts.CMF002Q, "CMF002_16",
            "/view/cmf/002/q/index.xhtml");
    public static final Program CMF002R = new Program(WebAppId.COM, ProgramIdConsts.CMF002R, "CMF002_17",
            "/view/cmf/002/r/index.xhtml");
    public static final Program CMF002S = new Program(WebAppId.COM, ProgramIdConsts.CMF002S, "CMF002_18",
            "/view/cmf/002/s/index.xhtml");
    public static final Program CMF002T = new Program(WebAppId.COM, ProgramIdConsts.CMF002T, "CMF002_19",
            "/view/cmf/002/t/index.xhtml");
    public static final Program CMF002V1 = new Program(WebAppId.COM, ProgramIdConsts.CMF002V1, "CMF002_21",
            "/view/cmf/002/v1/index.xhtml");
    public static final Program CMF002X = new Program(WebAppId.COM, ProgramIdConsts.CMF002X, "CMF002_23",
            "/view/cmf/002/x/index.xhtml");
    public static final Program CMF002Y = new Program(WebAppId.COM, ProgramIdConsts.CMF002Y, "CMF002_24",
            "/view/cmf/002/y/index.xhtml");
    public static final Program CMF002V2 = new Program(WebAppId.COM, ProgramIdConsts.CMF002V2, "CMF002_21",
            "/view/cmf/002/v2/index.xhtml");
    public static final Program CMF002W = new Program(WebAppId.COM, ProgramIdConsts.CMF002W, "CMF002_529",
            "/view/cmf/002/w/index.xhtml");

    /**
     * CMF003
     */
    public static final Program CMF003A = new Program(WebAppId.COM, ProgramIdConsts.CMF003A, "CMF003_1",
            "/view/cmf/003/a/index.xhtml");
    public static final Program CMF003B = new Program(WebAppId.COM, ProgramIdConsts.CMF003B, "CMF003_2",
            "/view/cmf/003/b/index.xhtml");
    public static final Program CMF003C = new Program(WebAppId.COM, ProgramIdConsts.CMF003C, "CMF003_633",
            "/view/cmf/003/c/index.xhtml");
    public static final Program CMF003F = new Program(WebAppId.COM, ProgramIdConsts.CMF003F, "CMF003_6",
            "/view/cmf/003/f/index.xhtml");
    public static final Program CMF003I = new Program(WebAppId.COM, ProgramIdConsts.CMF003I, "CMF003_9",
            "/view/cmf/003/i/index.xhtml");

    /**
     * CMF004
     */
    public static final Program CMF004A = new Program(WebAppId.COM, ProgramIdConsts.CMF004A, "CMF004_1",
            "/view/cmf/004/a/index.xhtml");
    public static final Program CMF004B = new Program(WebAppId.COM, ProgramIdConsts.CMF004B, "CMF004_2",
            "/view/cmf/004/b/index.xhtml");
    public static final Program CMF004C = new Program(WebAppId.COM, ProgramIdConsts.CMF004C, "CMF004_3",
            "/view/cmf/004/c/index.xhtml");
    public static final Program CMF004D = new Program(WebAppId.COM, ProgramIdConsts.CMF004D, "CMF004_4",
            "/view/cmf/004/d/index.xhtml");
    public static final Program CMF004I = new Program(WebAppId.COM, ProgramIdConsts.CMF004I, "CMF004_9",
            "/view/cmf/004/i/index.xhtml");
    public static final Program CMF004J = new Program(WebAppId.COM, ProgramIdConsts.CMF004J, "CMF004_10",
            "/view/cmf/004/j/index.xhtml");

    /**
     * CMM048
     */
    public static final Program CMM048A = new Program(WebAppId.COM, ProgramIdConsts.CMM048A, "CMM048_1",
            "/view/cmm/048/a/index.xhtml");
    public static final Program CMM048B = new Program(WebAppId.COM, ProgramIdConsts.CMM048B, "CMM048_2",
            "/view/cmm/048/b/index.xhtml");
    public static final Program CMM048E = new Program(WebAppId.COM, ProgramIdConsts.CMM048E, "CMM048_113",
            "/view/cmm/048/e/index.xhtml");
    public static final Program CMM048F = new Program(WebAppId.COM, ProgramIdConsts.CMM048F, "CMM048_112",
            "/view/cmm/048/f/index.xhtml");
    
    /**
     * CMM049
     */
    public static final Program CMM049A = new Program(WebAppId.COM, ProgramIdConsts.CMM049A, "CMM049_1",
            "/view/cmm/049/a/index.xhtml");
    public static final Program CMM049B = new Program(WebAppId.COM, ProgramIdConsts.CMM049B, "CMM049_2",
            "/view/cmm/049/b/index.xhtml");
    
    /**
     * CMM053
     */
    public static final Program CMM053A = new Program(WebAppId.COM, ProgramIdConsts.CMM053A, "CMM053_1",
            "/view/cmm/053/a/index.xhtml");
    public static final Program CMM053B = new Program(WebAppId.COM, ProgramIdConsts.CMM053B, "CMM053_1",
            "/view/cmm/053/b/index.xhtml");

    /**
     * KRD001
     */
    public static final Program KDR001A = new Program(WebAppId.AT, ProgramIdConsts.KDR001A, "KDR001_51",
            "/view/kdr/001/a/index.xhtml");
    public static final Program KRD001B = new Program(WebAppId.AT, ProgramIdConsts.KDR001B, "KDR001_52",
            "/view/kdr/001/b/index.xhtml");

    /**
     * KRD002
     */
    public static final Program KDR002A = new Program(WebAppId.AT, ProgramIdConsts.KDR002A, "KDR002_10",
            "/view/kdr/002/a/index.xhtml");
    /**
     * KRD003
     */
    public static final Program KDR003A = new Program(WebAppId.AT, ProgramIdConsts.KDR003A, "KDR003_100",
            "/view/kdr/003/a/index.xhtml");

    /**
     * KRD004
     */
    public static final Program KDR004A = new Program(WebAppId.AT, ProgramIdConsts.KDR004A, "KDR004_100",
            "/view/kdr/004/a/index.xhtml");


    /**
     * CMM001E
     */
    public static final Program CMM001E = new Program(WebAppId.COM, ProgramIdConsts.CMM001E, "CMM001_68",
            "/view/cmm/001/e/index.xhtml");
    /**
     * KMW003
     */
    public static final Program KMW003A = new Program(WebAppId.AT, ProgramIdConsts.KMW003A, "KMW003_1",
            "/view/kmw/003/a/index.xhtml");
    /**
     * KMW006
     */
    public static final Program KMW006A = new Program(WebAppId.AT, ProgramIdConsts.KMW006A, "KMW006_41",
            "/view/kmw/006/a/index.xhtml");
    public static final Program KMW006C = new Program(WebAppId.AT, ProgramIdConsts.KMW006C, "KMW006_43",
            "/view/kmw/006/c/index.xhtml");
    public static final Program KMW006D = new Program(WebAppId.AT, ProgramIdConsts.KMW006D, "KMW006_44",
            "/view/kmw/006/d/index.xhtml");
    public static final Program KMW006E = new Program(WebAppId.AT, ProgramIdConsts.KMW006E, "KMW006_45",
            "/view/kmw/006/e/index.xhtml");
    public static final Program KMW006F = new Program(WebAppId.AT, ProgramIdConsts.KMW006F, "KMW006_46",
            "/view/kmw/006/f/index.xhtml");

    /**
     * KMR001
     */
    public static final Program KMR001A = new Program(WebAppId.AT, ProgramIdConsts.KMR001A, "KMR001_78",
            "/view/kmr/001/a/index.xhtml");
    public static final Program KMR001B = new Program(WebAppId.AT, ProgramIdConsts.KMR001B, "KMR001_1",
            "/view/kmr/001/b/index.xhtml");
    public static final Program KMR001C = new Program(WebAppId.AT, ProgramIdConsts.KMR001C, "KMR001_2",
            "/view/kmr/001/c/index.xhtml");
    public static final Program KMR001D = new Program(WebAppId.AT, ProgramIdConsts.KMR001D, "KMR001_38",
            "/view/kmr/001/d/index.xhtml");

    /**
     * KMR003
     */
    public static final Program KMR003A = new Program(WebAppId.AT, ProgramIdConsts.KMR003A, "KMR003_1",
            "/view/kmr/003/a/index.xhtml");
    public static final Program KMR003B = new Program(WebAppId.AT, ProgramIdConsts.KMR003B, "KMR003_26",
            "/view/kmr/003/b/index.xhtml");

    /**
     * KMR004
     */
    public static final Program KMR004A = new Program(WebAppId.AT, ProgramIdConsts.KMR004A, "KMR004_40",
            "/view/kmr/004/a/index.xhtml");

    /**
     * KWR002
     */
    public static final Program KWR002A = new Program(WebAppId.AT, ProgramIdConsts.KWR002A, "KWR002_1",
            "/view/kwr/002/a/index.xhtml");
    public static final Program KWR002B = new Program(WebAppId.AT, ProgramIdConsts.KWR002B, "KWR002_2",
            "/view/kwr/002/b/index.xhtml");
    public static final Program KWR002C = new Program(WebAppId.AT, ProgramIdConsts.KWR002C, "KWR002_3",
            "/view/kwr/002/c/index.xhtml");
    public static final Program KWR002D = new Program(WebAppId.AT, ProgramIdConsts.KWR002D, "KWR002_4",
            "/view/kwr/002/d/index.xhtml");
    public static final Program KWR002E = new Program(WebAppId.AT, ProgramIdConsts.KWR002E, "KWR002_5",
            "/view/kwr/002/e/index.xhtml");
    public static final Program KWR002F = new Program(WebAppId.AT, ProgramIdConsts.KWR002F, "KWR002_6",
            "/view/kwr/002/f/index.xhtml");


    /**
     * KDM001
     */
    public static final Program KDM001A = new Program(WebAppId.AT, ProgramIdConsts.KDM001A, "KDM001_131",
            "/view/kdm/001/a/index.xhtml");
    public static final Program KDM001B = new Program(WebAppId.AT, ProgramIdConsts.KDM001B, "KDM001_132",
            "/view/kdm/001/b/index.xhtml");
    public static final Program KDM001D = new Program(WebAppId.AT, ProgramIdConsts.KDM001D, "KDM001_134",
            "/view/kdm/001/d/index.xhtml");
    public static final Program KDM001E = new Program(WebAppId.AT, ProgramIdConsts.KDM001E, "KDM001_135",
            "/view/kdm/001/e/index.xhtml");
    public static final Program KDM001F = new Program(WebAppId.AT, ProgramIdConsts.KDM001F, "KDM001_136",
            "/view/kdm/001/f/index.xhtml");
    public static final Program KDM001G = new Program(WebAppId.AT, ProgramIdConsts.KDM001G, "KDM001_137",
            "/view/kdm/001/g/index.xhtml");
    public static final Program KDM001H = new Program(WebAppId.AT, ProgramIdConsts.KDM001H, "KDM001_138",
            "/view/kdm/001/h/index.xhtml");
    public static final Program KDM001I = new Program(WebAppId.AT, ProgramIdConsts.KDM001I, "KDM001_139",
            "/view/kdm/001/i/index.xhtml");
    public static final Program KDM001J = new Program(WebAppId.AT, ProgramIdConsts.KDM001J, "KDM001_140",
            "/view/kdm/001/j/index.xhtml");
    public static final Program KDM001K = new Program(WebAppId.AT, ProgramIdConsts.KDM001K, "KDM001_141",
            "/view/kdm/001/k/index.xhtml");
    public static final Program KDM001L = new Program(WebAppId.AT, ProgramIdConsts.KDM001L, "KDM001_142",
            "/view/kdm/001/l/index.xhtml");
    public static final Program KDM001M = new Program(WebAppId.AT, ProgramIdConsts.KDM001M, "KDM001_143",
            "/view/kdm/001/m/index.xhtml");

    /**
     * CDL027
     */
    public static final Program CDL027TEST = new Program(WebAppId.COM, ProgramIdConsts.CDL027TEST, "CDL027_3",
            "/view/cdl/027/demo/index.xhtml");
    public static final Program CDL027 = new Program(WebAppId.COM, ProgramIdConsts.CDL027, "CDL027_3",
            "/view/cdl/027/a/index.xhtml");

    /**
     * KWR008
     **/
    public static final Program KWR008A = new Program(WebAppId.AT, ProgramIdConsts.KWR008A, "KWR008_1",
            "/view/kwr/008/a/index.xhtml");
    public static final Program KWR008B = new Program(WebAppId.AT, ProgramIdConsts.KWR008B, "KWR008_2",
            "/view/kwr/008/b/index.xhtml");

    /**
     * KAF018
     */
    public static final Program KAF018A = new Program(WebAppId.AT, ProgramIdConsts.KAF018A, "KAF018_1",
            "/view/kaf/018/a/index.xhtml");
    public static final Program KAF018B = new Program(WebAppId.AT, ProgramIdConsts.KAF018B, "KAF018_2",
            "/view/kaf/018/b/index.xhtml");
    public static final Program KAF018C = new Program(WebAppId.AT, ProgramIdConsts.KAF018C, "KAF018_3",
            "/view/kaf/018/c/index.xhtml");
    public static final Program KAF018D = new Program(WebAppId.AT, ProgramIdConsts.KAF018D, "KAF018_4",
            "/view/kaf/018/d/index.xhtml");
    public static final Program KAF018E = new Program(WebAppId.AT, ProgramIdConsts.KAF018E, "KAF018_5",
            "/view/kaf/018/e/index.xhtml");
    public static final Program KAF018F = new Program(WebAppId.AT, ProgramIdConsts.KAF018F, "KAF018_6",
            "/view/kaf/018/f/index.xhtml");
    public static final Program KAF018G = new Program(WebAppId.AT, ProgramIdConsts.KAF018G, "KAF018_7",
            "/view/kaf/018/g/index.xhtml");
    public static final Program KAF018H = new Program(WebAppId.AT, ProgramIdConsts.KAF018H, "KAF018_8",
            "/view/kaf/018/h/index.xhtml");
    public static final Program KAF018I = new Program(WebAppId.AT, ProgramIdConsts.KAF018I, "KAF018_9",
            "/view/kaf/018/i/index.xhtml");

    /**
     * KAF012
     */
    public static final Program KAF012A = new Program(WebAppId.AT, ProgramIdConsts.KAF012A, null,
            "/view/kaf/012/a/index.xhtml");
    public static final Program KAF012B = new Program(WebAppId.AT, ProgramIdConsts.KAF012B, null,
            "/view/kaf/012/b/index.xhtml");

    /**
     * KAF012
     */
    public static final Program KAFS12B = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS12B, null,
            "/kaf/S12/b/index.xhtml");

    /**
     * KDM002.
     */
    public static final Program KDM002A = new Program(WebAppId.AT, ProgramIdConsts.KDM002A, "KDM002_1",
            "/view/kdm/002/a/index.xhtml");
    public static final Program KDM002B = new Program(WebAppId.AT, ProgramIdConsts.KDM002B, "KDM002_2",
            "/view/kdm/002/b/index.xhtml");


    /**
     * CMF005
     */
    public static final Program CMF005A = new Program(WebAppId.COM, ProgramIdConsts.CMF005A, "CMF005_1",
            "/view/cmf/005/a/index.xhtml");
    public static final Program CMF005B = new Program(WebAppId.COM, ProgramIdConsts.CMF005B, "CMF005_2",
            "/view/cmf/005/b/index.xhtml");
    public static final Program CMF005C = new Program(WebAppId.COM, ProgramIdConsts.CMF005C, "CMF005_3",
            "/view/cmf/005/c/index.xhtml");
    public static final Program CMF005F = new Program(WebAppId.COM, ProgramIdConsts.CMF005F, "CMF005_6",
            "/view/cmf/005/f/index.xhtml");
    public static final Program CMF005I = new Program(WebAppId.COM, ProgramIdConsts.CMF005I, "CMF005_9",
            "/view/cmf/005/i/index.xhtml");


    /**
     * CMF006
     */
    public static final Program CMF006A = new Program(WebAppId.COM, ProgramIdConsts.CMF006A, "CMF006_1",
            "/view/cmf/006/a/index.xhtml");
    public static final Program CMF006B = new Program(WebAppId.COM, ProgramIdConsts.CMF006B, "CMF006_2",
            "/view/cmf/006/b/index.xhtml");

    /**
     * KFP001
     */
    public static final Program KFP001A = new Program(WebAppId.AT, ProgramIdConsts.KFP001A, "KFP001_1",
            "/view/kfp/001/a/index.xhtml");
    public static final Program KFP001B = new Program(WebAppId.AT, ProgramIdConsts.KFP001B, "KFP001_1",
            "/view/kfp/001/b/index.xhtml");
    public static final Program KFP001C = new Program(WebAppId.AT, ProgramIdConsts.KFP001C, "KFP001_1",
            "/view/kfp/001/c/index.xhtml");
    public static final Program KFP001D = new Program(WebAppId.AT, ProgramIdConsts.KFP001D, "KFP001_1",
            "/view/kfp/001/d/index.xhtml");
    public static final Program KFP001E = new Program(WebAppId.AT, ProgramIdConsts.KFP001E, "KFP001_2",
            "/view/kfp/001/e/index.xhtml");
    public static final Program KFP001F = new Program(WebAppId.AT, ProgramIdConsts.KFP001F, "KFP001_3",
            "/view/kfp/001/f/index.xhtml");
    public static final Program KFP001G = new Program(WebAppId.AT, ProgramIdConsts.KFP001G, "KFP001_4",
            "/view/kfp/001/g/index.xhtml");
    public static final Program KFP001H = new Program(WebAppId.AT, ProgramIdConsts.KFP001H, "KFP001_1",
            "/view/kfp/001/h/index.xhtml");


    // KDL030
    public static final Program KDL030 = new Program(WebAppId.AT, ProgramIdConsts.KDL030, "KDL030_1",
            "/view/kdl/030/a/index.xhtml");
    // KDL034
    public static final Program KDL034 = new Program(WebAppId.AT, ProgramIdConsts.KDL034, "KDL034_1",
            "/view/kdl/034/a/index.xhtml");

    /**
     * CCG009
     */
    public static final Program CCG009A = new Program(WebAppId.COM, ProgramIdConsts.CCG009A, "CCG009_1",
            "/view/ccg/009/index.xhtml");

    /**
     * CLI001
     */
    public static final Program CLI001A = new Program(WebAppId.COM, ProgramIdConsts.CLI001A, "CLI001_1",
            "/view/cli/001/a/index.xhtml");
    public static final Program CLI001B = new Program(WebAppId.COM, ProgramIdConsts.CLI001B, "CLI001_2",
            "/view/cli/001/b/index.xhtml");

    /**
     * CLI002
     */
    public static final Program CLI002A = new Program(WebAppId.COM,
            ProgramIdConsts.CLI002A,
            "CLI002_1",
            "/view/cli/002/a/index.xhtml");

    /**
     * CLI003
     */
    public static final Program CLI003G = new Program(WebAppId.COM, ProgramIdConsts.CLI003G, "CLI003_72",
            "/view/cli/003/g/index.xhtml");
    public static final Program CLI003H = new Program(WebAppId.COM, ProgramIdConsts.CLI003H, "CLI003_47",
            "/view/cli/003/h/index.xhtml");
    public static final Program CLI003A = new Program(WebAppId.COM, ProgramIdConsts.CLI003A, "CLI003_64",
            "/view/cli/003/a/index.xhtml");
    public static final Program CLI003B = new Program(WebAppId.COM, ProgramIdConsts.CLI003B, "CLI003_64",
            "/view/cli/003/b/index.xhtml");
    public static final Program CLI003C = new Program(WebAppId.COM, ProgramIdConsts.CLI003C, "CLI003_87",
            "/view/cli/003/c/index.xhtml");
    public static final Program CLI003F = new Program(WebAppId.COM, ProgramIdConsts.CLI003F, "CLI003_64",
            "/view/cli/003/f/index.xhtml");

    /**
     * KTG031
     */
    public static final Program KTG031A = new Program(WebAppId.COM, ProgramIdConsts.KTG031A, "KTG031_1",
            "/view/ktg/031/a/index.xhtml");
    public static final Program KTG031B = new Program(WebAppId.COM, ProgramIdConsts.KTG031B, "KTG031_5",
            "/view/ktg/031/b/index.xhtml");
    /**
     * KDL029
     */
    public static final Program KDL029A = new Program(WebAppId.AT, ProgramIdConsts.KDL029A, "KDL029_1",
            "/view/kdl/029/a/index.xhtml");

    /**
     * CAS004
     */
    public static final Program CAS004A = new Program(WebAppId.COM, ProgramIdConsts.CAS004A, "CAS004_1",
            "/view/cas/004/a/index.xhtml");
    public static final Program CAS004B = new Program(WebAppId.COM, ProgramIdConsts.CAS004B, "CAS004_2",
            "/view/cas/004/b/index.xhtml");

    /**
     * KMP001
     */
    public static final Program KMP001A = new Program(WebAppId.AT, ProgramIdConsts.KMP001A, "KMP001_17",
            "/view/kmp/001/a/index.xhtml");
    public static final Program KMP001B = new Program(WebAppId.AT, ProgramIdConsts.KMP001B, "KMP001_18",
            "/view/kmp/001/b/index.xhtml");
    public static final Program KMP001C = new Program(WebAppId.AT, ProgramIdConsts.KMP001C, "KMP001_19",
            "/view/kmp/001/c/index.xhtml");
    public static final Program KMP001D = new Program(WebAppId.AT, ProgramIdConsts.KMP001D, "KMP001_7",
            "/view/kmp/001/d/index.xhtml");
    public static final Program KMP001E = new Program(WebAppId.AT, ProgramIdConsts.KMP001E, "KMP001_70",
            "/view/kmp/001/e/index.xhtml");
    public static final Program KMP001H = new Program(WebAppId.AT, ProgramIdConsts.KMP001H, "KMP001_106",
            "/view/kmp/001/h/index.xhtml");

    public static final Program KMP001I = new Program(WebAppId.AT, ProgramIdConsts.KMP001I, "KMP001_160",
            "/view/kmp/001/i/index.xhtml");
    /**
     * KDP001
     */
    public static final Program KDP001A = new Program(WebAppId.AT, ProgramIdConsts.KDP001A, "KDP001_1",
            "/view/kdp/001/a/index.xhtml");
    /**
     * KDP003
     */
    public static final Program KDP003A = new Program(WebAppId.AT, ProgramIdConsts.KDP003A, "KDP002_2",
            "/view/kdp/003/a/index.xhtml");
    public static final Program KDP003F = new Program(WebAppId.AT, ProgramIdConsts.KDP003F, "KDP002_9",
            "/view/kdp/003/f/index.xhtml");
    public static final Program KDP003K = new Program(WebAppId.AT, ProgramIdConsts.KDP003K, "KDP002_14",
            "/view/kdp/003/k/index.xhtml");
    public static final Program KDP003S = new Program(WebAppId.AT, ProgramIdConsts.KDP003S, "KDP002_22",
            "/view/kdp/003/s/index.xhtml");
    public static final Program KDP003P = new Program(WebAppId.AT, ProgramIdConsts.KDP003P, "KDP003_54",
            "/view/kdp/003/p/index.xhtml");
    public static final Program KDP003Q = new Program(WebAppId.AT, ProgramIdConsts.KDP003Q, "KDP003_59",
            "/view/kdp/003/q/index.xhtml");
    public static final Program KDP003R = new Program(WebAppId.AT, ProgramIdConsts.KDP003R, "KDP003_50",
            "/view/kdp/003/r/index.xhtml");
    public static final Program KDP003M = new Program(WebAppId.AT, ProgramIdConsts.KDP003M, "KDP002_16",
            "/view/kdp/003/m/index.xhtml");
    /**
     * KDP004
     */
    public static final Program KDP004A = new Program(WebAppId.AT, ProgramIdConsts.KDP004A, "KDP002_3",
            "/view/kdp/004/a/index.xhtml");

    public static final Program KDP004G = new Program(WebAppId.AT, ProgramIdConsts.KDP004G, "KDP002_10",
            "/view/kdp/004/g/index.xhtml");

    /**
     * KDP005
     */
    public static final Program KDP005A = new Program(WebAppId.AT, ProgramIdConsts.KDP005A, "KDP002_4",
            "/view/kdp/005/a/index.xhtml");
    public static final Program KDP005H = new Program(WebAppId.AT, ProgramIdConsts.KDP005H, "KDP002_11",
            "/view/kdp/005/h/index.xhtml");
    public static final Program KDP005I = new Program(WebAppId.AT, ProgramIdConsts.KDP005I, "KDP002_12",
            "/view/kdp/005/i/index.xhtml");


    public static final Program CMF007 = new Program(WebAppId.COM, ProgramIdConsts.CMF007, "CMF007_1",
            "/view/cmf/007/a/index.xhtml");

    /**
     * JCM007
     */
    public static final Program JCM007A = new Program(WebAppId.HR, ProgramIdConsts.JCM007A, "JCM007_A",
            "/view/jcm/007/a/index.xhtml");

    public static final Program JCM007Z = new Program(WebAppId.HR, ProgramIdConsts.JCM007Z, "JCM007_Z",
            "/view/jcm/007/z/index.xhtml");

    /**
     * KDP010
     */
    public static final Program KDP010A = new Program(WebAppId.AT, ProgramIdConsts.KDP010A, "KDP010_142",
            "/view/kdp/010/a/index.xhtml");

    public static final Program KDP010B = new Program(WebAppId.AT, ProgramIdConsts.KDP010B, "KDP010_2",
            "/view/kdp/010/b/index.xhtml");

    public static final Program KDP010C = new Program(WebAppId.AT, ProgramIdConsts.KDP010C, "KDP010_3",
            "/view/kdp/010/c/index.xhtml");

    public static final Program KDP010D = new Program(WebAppId.AT, ProgramIdConsts.KDP010D, "KDP010_4",
            "/view/kdp/010/d/index.xhtml");

    public static final Program KDP010E = new Program(WebAppId.AT, ProgramIdConsts.KDP010E, "KDP010_5",
            "/view/kdp/010/e/index.xhtml");

    public static final Program KDP010F = new Program(WebAppId.AT, ProgramIdConsts.KDP010F, "KDP010_6",
            "/view/kdp/010/f/index.xhtml");

    public static final Program KDP010G = new Program(WebAppId.AT, ProgramIdConsts.KDP010G, "KDP010_7",
            "/view/kdp/010/g/index.xhtml");

    public static final Program KDP010H = new Program(WebAppId.AT, ProgramIdConsts.KDP010H, "KDP010_8",
            "/view/kdp/010/h/index.xhtml");

    public static final Program KDP010I = new Program(WebAppId.AT, ProgramIdConsts.KDP010I, "KDP010_9",
            "/view/kdp/010/i/index.xhtml");
    
    public static final Program KDP010J = new Program(WebAppId.AT, ProgramIdConsts.KDP010J, "KDP010_140",
            "/view/kdp/010/j/index.xhtml");

    public static final Program KDP010K = new Program(WebAppId.AT, ProgramIdConsts.KDP010K, "KDP010_143",
            "/view/kdp/010/k/index.xhtml");
    
    /**
     * KDP011
     */
    public static final Program KDP011A = new Program(WebAppId.AT, ProgramIdConsts.KDP011A, "KDP011_1",
            "/view/kdp/011/a/index.xhtml");

    /**
     * JHC002
     */
    public static final Program JHC002A = new Program(WebAppId.HR, ProgramIdConsts.JHC002A, "JHC002_A",
            "/view/jhc/002/a/index.xhtml");
    public static final Program JHC002B = new Program(WebAppId.HR, ProgramIdConsts.JHC002B, "JHC002_B",
            "/view/jhc/002/b/index.xhtml");
    public static final Program JHC002C = new Program(WebAppId.HR, ProgramIdConsts.JHC002C, "JHC002_C",
            "/view/jhc/002/c/index.xhtml");

    /**
     * JMM017
     */
    public static final Program JMM017A = new Program(WebAppId.HR, ProgramIdConsts.JMM017A, "JMM017_A",
            "/view/jmm/017/a/index.xhtml");
    public static final Program JMM017B = new Program(WebAppId.HR, ProgramIdConsts.JMM017B, "JMM017_B",
            "/view/jmm/017/b/index.xhtml");
    public static final Program JMM017C = new Program(WebAppId.HR, ProgramIdConsts.JMM017C, "JMM017_C",
            "/view/jmm/017/c/index.xhtml");

    /**
     * JMM018
     */
    public static final Program JMM018A = new Program(WebAppId.HR, ProgramIdConsts.JMM018A, "JMM018_A",
            "/view/jmm/018/a/index.xhtml");

    public static final Program JMM018C = new Program(WebAppId.HR, ProgramIdConsts.JMM018A, "JMM018_C",
            "/view/jmm/018/c/index.xhtml");

    public static final Program JMM018Y = new Program(WebAppId.HR, ProgramIdConsts.JMM018Y, "JMM018_Y",
            "/view/jmm/018/y/index.xhtml");

    /**
     * JHN001
     */
    public static final Program JHN001A = new Program(WebAppId.HR, ProgramIdConsts.JHN001A, "JHN001_A",
            "/view/jhn/001/a/index.xhtml");
    public static final Program JHN001B = new Program(WebAppId.HR, ProgramIdConsts.JHN001B, "JHN001_B",
            "/view/jhn/001/b/index.xhtml");
    public static final Program JHN001C = new Program(WebAppId.HR, ProgramIdConsts.JHN001C, "JHN001_C",
            "/view/jhn/001/c/index.xhtml");
    public static final Program JHN001D = new Program(WebAppId.HR, ProgramIdConsts.JHN001D, "JHN001_D",
            "/view/jhn/001/d/index.xhtml");
    public static final Program JHN001F = new Program(WebAppId.HR, ProgramIdConsts.JHN001F, "JHN001_F",
            "/view/jhn/001/f/index.xhtml");

    /**
     * JHN003
     */
    public static final Program JHN003A = new Program(WebAppId.HR, ProgramIdConsts.JHN003A, "JHN003_A",
            "/view/jhn/003/a/index.xhtml");

    /**
     * JHN011
     */
    public static final Program JHN011B = new Program(WebAppId.HR, ProgramIdConsts.JHN011B, "JHN011_B",
            "/view/jhn/011/b/index.xhtml");
    public static final Program JHN011C = new Program(WebAppId.HR, ProgramIdConsts.JHN011C, "JHN011_C",
            "/view/jhn/011/c/index.xhtml");


    /**
     * The Constant CCG007A. IN MOBILE
     */
    public static final Program CCGS07A = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07A, "CCG007_51",
            "/ccg/007/a", ProgramIdConsts.CCG007A);

    /**
     * The Constant CCG007D. IN MOBILE
     */
    public static final Program CCGS07B = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07B, "CCG007_54",
            "/ccg/007/b", ProgramIdConsts.CCG007D);

    /**
     * The Constant CCG007E. IN MOBILE
     */
    public static final Program CCGS07C = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07C, "CCG007_55",
            "/ccg/007/c", ProgramIdConsts.CCG007E);

    /**
     * The Constant CCG007F. IN MOBILE
     */
    public static final Program CCGS07D = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07D, "CCG007_56",
            "/ccg/007/d", ProgramIdConsts.CCG007F);

    /**
     * The Constant CCG007G. IN MOBILE
     */
    public static final Program CCGS07F = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07F, "CCG007_57",
            "/ccg/007/f", ProgramIdConsts.CCG007G);

    /**
     * The Constant CCG007H. IN MOBILE
     */
    public static final Program CCGS07E = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS07E, "CCG007_58",
            "/ccg/007/e", ProgramIdConsts.CCG007H);

    //スマホ②
    public static final Program KAFS04A = new Program(WebAppId.MOBI,ProgramIdConsts.KAFS04A,"KAFS04A",
            "/kaf/s04/a");
    public static final Program KAFS05A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS05A, "KAFS05A",
            "/kaf/s05/a");
    public static final Program KAFS06A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS06A, "KAFS06A",
            "/kaf/s06/a");
    public static final Program KAFS10A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS10A, "KAFS10A",
            "/kaf/s10/a");
    public static final Program KAFS07A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS07A, "KAFS07A",
            "/kaf/s07/a");
    public static final Program KAFS08A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08A, "KAFS08A",
            "/kaf/s08/a");
    public static final Program KAFS09A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS09A, "KAFS09A",
            "/kaf/s09/a");
    public static final Program KAFS12A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS12A, "KAFS12A",
            "/kaf/s12/a");
    public static final Program KAFS05B = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS05B, "KAFS05B",
            "/kaf/s05/b");
    public static final Program KAFS10B = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS10B, "KAFS10B",
            "/kaf/s10/b");
    public static final Program KAFS20A = new Program(WebAppId.MOBI,ProgramIdConsts.KAFS20A,"KAFS20A",
            "/kaf/s20/a");
    public static final Program CMMS45B = new Program(WebAppId.MOBI, ProgramIdConsts.CMMS45B, "CMMS45B",
            "/cmm/s45/b");
    public static final Program CMMS45C = new Program(WebAppId.MOBI, ProgramIdConsts.CMMS45C, "CMMS45C",
            "/cmm/s45/c");
    public static final Program CMMS45D = new Program(WebAppId.MOBI, ProgramIdConsts.CMMS45D, "CMMS45D",
            "/cmm/s45/d");
    public static final Program CMMS45E = new Program(WebAppId.MOBI, ProgramIdConsts.CMMS45E, "CMMS45E",
            "/cmm/s45/e");
    public static final Program CCGS33 = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS33, "",
            "/ccg/033/a");
    public static final Program KDWS03A = new Program(WebAppId.MOBI, ProgramIdConsts.KDWS03A, "",
            "/kdw/s03/a");
    public static final Program KAFS08A1 = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08A1, "KAFS08_2",
            "kaf/s08/a1");
    public static final Program KAFS08A2 = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08A2, "KAFS08_3",
            "kaf/s08/a2");
    public static final Program KAFS08B = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08B, "KAFS08_7",
            "kaf/s08/b");
    public static final Program KAFS08C = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08C, "KAFS08_8",
            "kaf/s08/c");
    public static final Program KAFS08D = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS08D, "KAFS08_9",
            "kaf/s08/d");
    public static final Program KAFS02A = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS02A, "KAFS02A",
            "/kaf/s02/a");
    public static final Program KAFS02C = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS02C, "KAFS02C",
            "/kaf/s02/c");
    public static final Program KAFS11 = new Program(WebAppId.MOBI, ProgramIdConsts.KAFS11A, "KAFS11A",
            "/kaf/s11/a");

    public static final Program KDPS01A = new Program(WebAppId.MOBI, ProgramIdConsts.KDPS01A, "KDPS01_1",
            "/kdp/s01/a");
    public static final Program KDPS01B = new Program(WebAppId.MOBI, ProgramIdConsts.KDPS01B, "KDPS01_5",
            "/kdp/s01/b");
    public static final Program KDPS01C = new Program(WebAppId.MOBI, ProgramIdConsts.KDPS01C, "KDPS01_5",
            "/kdp/s01/c");
    public static final Program KDPS01S = new Program(WebAppId.MOBI, ProgramIdConsts.KDPS01S, "KDPS01_22",
            "/kdp/s01/s");
    public static final Program KDPS01T = new Program(WebAppId.MOBI, ProgramIdConsts.KDPS01T, "KDPS01_23",
            "/kdp/s01/t");
    
    /** KSUS01 */
    public static final Program KSUS01A = new Program(WebAppId.MOBI, ProgramIdConsts.KSUS01A, "KSUS01A",
            "/ksu/s01/a");
    public static final Program KSUS01B = new Program(WebAppId.MOBI, ProgramIdConsts.KSUS01B, "KSUS01B",
            "/ksu/s01/b");

    /**
     * KSUS02
     */
    public static final Program KSUS02A = new Program(WebAppId.MOBI, ProgramIdConsts.KSUS02A, "KSUS02_23",
            "/ksu/s02/a");
    /**
     * CPS013
     */
    public static final Program CPS013A = new Program(WebAppId.COM, ProgramIdConsts.CPS013A, "CPS013_34",
            "/view/cps/013/a/index.xhtml");
    public static final Program CPS013B = new Program(WebAppId.COM, ProgramIdConsts.CPS013B, "CPS013_35",
            "/view/cps/013/b/index.xhtml");


    /**
     * QMM005
     */
    public static final Program QMM005A = new Program(WebAppId.PR, ProgramIdConsts.QMM005A, "QMM005_122",
            "/view/qmm/005/a/index.xhtml");
    public static final Program QMM005B = new Program(WebAppId.PR, ProgramIdConsts.QMM005B, "QMM005_123",
            "/view/qmm/005/b/index.xhtml");
    public static final Program QMM005D = new Program(WebAppId.PR, ProgramIdConsts.QMM005D, "QMM005_125",
            "/view/qmm/005/d/index.xhtml");
    public static final Program QMM005E = new Program(WebAppId.PR, ProgramIdConsts.QMM005E, "QMM005_27",
            "/view/qmm/005/e/index.xhtml");
    public static final Program QMM005F = new Program(WebAppId.PR, ProgramIdConsts.QMM005F, "QMM005_127",
            "/view/qmm/005/f/index.xhtml");

    /**
     * QMM039
     */
    public static final Program QMM039A = new Program(WebAppId.PR, ProgramIdConsts.QMM039A, "QMM039_1",
            "/view/qmm/039/a/index.xhtml");
    public static final Program QMM039B = new Program(WebAppId.PR, ProgramIdConsts.QMM039B, "QMM039_2",
            "/view/qmm/039/b/index.xhtml");
    public static final Program QMM039C = new Program(WebAppId.PR, ProgramIdConsts.QMM039C, "QMM039_3",
            "/view/qmm/039/c/index.xhtml");
    public static final Program QMM039D = new Program(WebAppId.PR, ProgramIdConsts.QMM039D, "QMM039_4",
            "/view/qmm/039/d/index.xhtml");

    /**
     * QMM031
     */
    public static final Program QMM031A = new Program(WebAppId.PR, ProgramIdConsts.QMM031A, "QMM031_1",
            "/view/qmm/031/a/index.xhtml");
    public static final Program QMM031D = new Program(WebAppId.PR, ProgramIdConsts.QMM031D, "QMM031_2",
            "/view/qmm/031/d/index.xhtml");
    public static final Program QMM031E = new Program(WebAppId.PR, ProgramIdConsts.QMM031E, "QMM031_3",
            "/view/qmm/031/e/index.xhtml");

    /**
     * QMM008
     */
    public static final Program QMM008A = new Program(WebAppId.PR, ProgramIdConsts.QMM008A, "QMM008_186",
            "/view/qmm/008/a/index.xhtml");
    public static final Program QMM008B = new Program(WebAppId.PR, ProgramIdConsts.QMM008B, "QMM008_186",
            "/view/qmm/008/b/index.xhtml");
    public static final Program QMM008C = new Program(WebAppId.PR, ProgramIdConsts.QMM008C, "QMM008_186",
            "/view/qmm/008/c/index.xhtml");
    public static final Program QMM008D = new Program(WebAppId.PR, ProgramIdConsts.QMM008D, "QMM008_187",
            "/view/qmm/008/d/index.xhtml");
    public static final Program QMM008E = new Program(WebAppId.PR, ProgramIdConsts.QMM008E, "QMM008_188",
            "/view/qmm/008/e/index.xhtml");
    public static final Program QMM008F = new Program(WebAppId.PR, ProgramIdConsts.QMM008F, "QMM008_189",
            "/view/qmm/008/f/index.xhtml");
    public static final Program QMM008G = new Program(WebAppId.PR, ProgramIdConsts.QMM008G, "QMM008_190",
            "/view/qmm/008/g/index.xhtml");
    public static final Program QMM008H = new Program(WebAppId.PR, ProgramIdConsts.QMM008H, "QMM008_191",
            "/view/qmm/008/h/index.xhtml");
    public static final Program QMM008I = new Program(WebAppId.PR, ProgramIdConsts.QMM008I, "QMM008_186",
            "/view/qmm/008/i/index.xhtml");
    public static final Program QMM008J = new Program(WebAppId.PR, ProgramIdConsts.QMM008J, "QMM008_192",
            "/view/qmm/008/j/index.xhtml");

    /**
     * QMM010
     */
    public static final Program QMM010A = new Program(WebAppId.PR, ProgramIdConsts.QMM010A, "QMM010_34",
            "/view/qmm/010/a/index.xhtml");
    public static final Program QMM010B = new Program(WebAppId.PR, ProgramIdConsts.QMM010B, "QMM010_35",
            "/view/qmm/010/b/index.xhtml");

    /**
     * QMM018
     */
    public static final Program QMM018A = new Program(WebAppId.PR, ProgramIdConsts.QMM018A, "QMM018_30",
            "/view/qmm/018/a/index.xhtml");
    public static final Program QMM018B = new Program(WebAppId.PR, ProgramIdConsts.QMM018B, "QMM018_31",
            "/view/qmm/018/b/index.xhtml");

    /**
     * QMM023
     */
    public static final Program QMM023A = new Program(WebAppId.PR, ProgramIdConsts.QMM023A, "QMM023_13",
            "/view/qmm/023/a/index.xhtml");

    /**
     * CMM015
     */
    public static final Program CMM015A = new Program(WebAppId.COM, ProgramIdConsts.CMM015A, "CMM015_1",
            "/view/cmm/015/a/index.xhtml");

    /**
     * QMM038
     */
    public static final Program QMM038A = new Program(WebAppId.PR, ProgramIdConsts.QMM038A, "QMM038_1",
            "/view/qmm/038/a/index.xhtml");

    /**
     * QMM002
     */
    public static final Program QMM002A = new Program(WebAppId.PR, ProgramIdConsts.QMM002A, "QMM002_34",
            "/view/qmm/002/a/index.xhtml");
    public static final Program QMM002B = new Program(WebAppId.PR, ProgramIdConsts.QMM002B, "QMM002_8",
            "/view/qmm/002/b/index.xhtml");
    public static final Program QMM002C = new Program(WebAppId.PR, ProgramIdConsts.QMM002C, "QMM002_5",
            "/view/qmm/002/c/index.xhtml");
    public static final Program QMM002D = new Program(WebAppId.PR, ProgramIdConsts.QMM002D, "QMM002_4",
            "/view/qmm/002/d/index.xhtml");

    /**
     * QMM003
     */
    public static final Program QMM003A = new Program(WebAppId.PR, ProgramIdConsts.QMM003A, "QMM003_36",
            "/view/qmm/003/a/index.xhtml");
    public static final Program QMM003B = new Program(WebAppId.PR, ProgramIdConsts.QMM003B, "QMM003_8",
            "/view/qmm/003/b/index.xhtml");
    public static final Program QMM003C = new Program(WebAppId.PR, ProgramIdConsts.QMM003C, "QMM003_37",
            "/view/qmm/003/c/index.xhtml");
    public static final Program QMM003D = new Program(WebAppId.PR, ProgramIdConsts.QMM003D, "QMM003_7",
            "/view/qmm/003/d/index.xhtml");
    public static final Program QMM003E = new Program(WebAppId.PR, ProgramIdConsts.QMM003E, "QMM003_4",
            "/view/qmm/003/e/index.xhtml");

    /**
     * QMM006
     */
    public static final Program QMM006A = new Program(WebAppId.PR, ProgramIdConsts.QMM006A, "QMM006_48",
            "/view/qmm/006/a/index.xhtml");
    public static final Program QMM006B = new Program(WebAppId.PR, ProgramIdConsts.QMM006B, "QMM006_49",
            "/view/qmm/006/b/index.xhtml");
    public static final Program QMM006C = new Program(WebAppId.PR, ProgramIdConsts.QMM006C, "QMM006_3",
            "/view/qmm/006/c/index.xhtml");

    /**
     * QMM016
     */
    public static final Program QMM016A = new Program(WebAppId.PR, ProgramIdConsts.QMM016A, "QMM016_101",
            "/view/qmm/016/a/index.xhtml");
    public static final Program QMM016B = new Program(WebAppId.PR, ProgramIdConsts.QMM016B, "QMM016_102",
            "/view/qmm/016/b/index.xhtml");
    public static final Program QMM016C = new Program(WebAppId.PR, ProgramIdConsts.QMM016C, "QMM016_103",
            "/view/qmm/016/c/index.xhtml");
    public static final Program QMM016D = new Program(WebAppId.PR, ProgramIdConsts.QMM016D, "QMM016_104",
            "/view/qmm/016/d/index.xhtml");
    public static final Program QMM016E = new Program(WebAppId.PR, ProgramIdConsts.QMM016E, "QMM016_105",
            "/view/qmm/016/e/index.xhtml");
    public static final Program QMM016F = new Program(WebAppId.PR, ProgramIdConsts.QMM016F, "QMM016_106",
            "/view/qmm/016/f/index.xhtml");
    public static final Program QMM016G = new Program(WebAppId.PR, ProgramIdConsts.QMM016G, "QMM016_107",
            "/view/qmm/016/g/index.xhtml");
    public static final Program QMM016H = new Program(WebAppId.PR, ProgramIdConsts.QMM016H, "QMM016_5",
            "/view/qmm/016/h/index.xhtml");
    public static final Program QMM016I = new Program(WebAppId.PR, ProgramIdConsts.QMM016I, "QMM016_108",
            "/view/qmm/016/i/index.xhtml");
    public static final Program QMM016J = new Program(WebAppId.PR, ProgramIdConsts.QMM016J, "QMM016_109",
            "/view/qmm/016/j/index.xhtml");
    public static final Program QMM016K = new Program(WebAppId.PR, ProgramIdConsts.QMM016K, "QMM016_110",
            "/view/qmm/016/k/index.xhtml");
    public static final Program QMM016L = new Program(WebAppId.PR, ProgramIdConsts.QMM016L, "QMM016_111",
            "/view/qmm/016/l/index.xhtml");
    public static final Program QMM016M = new Program(WebAppId.PR, ProgramIdConsts.QMM016M, "QMM016_6",
            "/view/qmm/016/m/index.xhtml");

    /**
     * QMM017
     */
    public static final Program QMM017A = new Program(WebAppId.PR, ProgramIdConsts.QMM017A, "QMM017_201",
            "/view/qmm/017/a/index.xhtml");
    public static final Program QMM017E = new Program(WebAppId.PR, ProgramIdConsts.QMM017E, "QMM017_202",
            "/view/qmm/017/e/index.xhtml");
    public static final Program QMM017F = new Program(WebAppId.PR, ProgramIdConsts.QMM017F, "QMM017_203",
            "/view/qmm/017/f/index.xhtml");
    public static final Program QMM017G = new Program(WebAppId.PR, ProgramIdConsts.QMM017G, "QMM017_204",
            "/view/qmm/017/g/index.xhtml");
    public static final Program QMM017H = new Program(WebAppId.PR, ProgramIdConsts.QMM017H, "QMM017_205",
            "/view/qmm/017/h/index.xhtml");
    public static final Program QMM017I = new Program(WebAppId.PR, ProgramIdConsts.QMM017I, "QMM017_206",
            "/view/qmm/017/i/index.xhtml");
    public static final Program QMM017J = new Program(WebAppId.PR, ProgramIdConsts.QMM017J, "QMM017_207",
            "/view/qmm/017/j/index.xhtml");
    public static final Program QMM017K = new Program(WebAppId.PR, ProgramIdConsts.QMM017K, "QMM017_208",
            "/view/qmm/017/k/index.xhtml");
    public static final Program QMM017L = new Program(WebAppId.PR, ProgramIdConsts.QMM017L, "QMM017_209",
            "/view/qmm/017/l/index.xhtml");
    public static final Program QMM017M = new Program(WebAppId.PR, ProgramIdConsts.QMM017M, "QMM017_201",
            "/view/qmm/017/m/index.xhtml");

    /**
     * QMM019
     */
    public static final Program QMM019A = new Program(WebAppId.PR, ProgramIdConsts.QMM019A, "QMM019_208",
            "/view/qmm/019/a/index.xhtml");
    public static final Program QMM019B = new Program(WebAppId.PR, ProgramIdConsts.QMM019B, "QMM019_209",
            "/view/qmm/019/b/index.xhtml");
    public static final Program QMM019C = new Program(WebAppId.PR, ProgramIdConsts.QMM019C, "QMM019_210",
            "/view/qmm/019/c/index.xhtml");
    public static final Program QMM019D = new Program(WebAppId.PR, ProgramIdConsts.QMM019D, "QMM019_211",
            "/view/qmm/019/d/index.xhtml");
    public static final Program QMM019E = new Program(WebAppId.PR, ProgramIdConsts.QMM019E, "QMM019_212",
            "/view/qmm/019/e/index.xhtml");
    public static final Program QMM019F = new Program(WebAppId.PR, ProgramIdConsts.QMM019F, "QMM019_213",
            "/view/qmm/019/f/index.xhtml");
    public static final Program QMM019G = new Program(WebAppId.PR, ProgramIdConsts.QMM019G, "QMM019_214",
            "/view/qmm/019/g/index.xhtml");
    public static final Program QMM019H = new Program(WebAppId.PR, ProgramIdConsts.QMM019H, "QMM019_215",
            "/view/qmm/019/h/index.xhtml");
    public static final Program QMM019I = new Program(WebAppId.PR, ProgramIdConsts.QMM019I, "QMM019_216",
            "/view/qmm/019/i/index.xhtml");
    public static final Program QMM019J = new Program(WebAppId.PR, ProgramIdConsts.QMM019J, "QMM019_217",
            "/view/qmm/019/j/index.xhtml");
    public static final Program QMM019K = new Program(WebAppId.PR, ProgramIdConsts.QMM019K, "QMM019_190",
            "/view/qmm/019/k/index.xhtml");
    public static final Program QMM019L = new Program(WebAppId.PR, ProgramIdConsts.QMM019L, "QMM019_192",
            "/view/qmm/019/l/index.xhtml");
    public static final Program QMM019M = new Program(WebAppId.PR, ProgramIdConsts.QMM019M, "QMM019_220",
            "/view/qmm/019/m/index.xhtml");
    public static final Program QMM019N = new Program(WebAppId.PR, ProgramIdConsts.QMM019N, "QMM019_221",
            "/view/qmm/019/n/index.xhtml");
    public static final Program QMM019O = new Program(WebAppId.PR, ProgramIdConsts.QMM019O, "QMM019_222",
            "/view/qmm/019/o/index.xhtml");
    public static final Program QMM019P = new Program(WebAppId.PR, ProgramIdConsts.QMM019P, "QMM019_223",
            "/view/qmm/019/p/index.xhtml");

    /**
     * QMM020
     */
    public static final Program QMM020A = new Program(WebAppId.PR, ProgramIdConsts.QMM020A, "QMM020_101",
            "/view/qmm/020/a/index.xhtml");
    public static final Program QMM020I = new Program(WebAppId.PR, ProgramIdConsts.QMM020I, "QMM020_2",
            "/view/qmm/020/i/index.xhtml");
    public static final Program QMM020J = new Program(WebAppId.PR, ProgramIdConsts.QMM020J, "QMM020_102",
            "/view/qmm/020/j/index.xhtml");
    public static final Program QMM020K = new Program(WebAppId.PR, ProgramIdConsts.QMM020K, "QMM020_103",
            "/view/qmm/020/k/index.xhtml");
    public static final Program QMM020L = new Program(WebAppId.PR, ProgramIdConsts.QMM020L, "QMM020_3",
            "/view/qmm/020/l/index.xhtml");
    public static final Program QMM020M = new Program(WebAppId.PR, ProgramIdConsts.QMM020M, "QMM020_104",
            "/view/qmm/020/m/index.xhtml");

    /**
     * QMM025
     */
    public static final Program QMM025A = new Program(WebAppId.PR, ProgramIdConsts.QMM025A, "QMM025_40",
            "/view/qmm/025/a/index.xhtml");

    /**
     * QMM035
     */
    public static final Program QMM035A = new Program(WebAppId.PR, ProgramIdConsts.QMM035A, "QMM035_1",
            "/view/qmm/035/a/index.xhtml");

    /**
     * QMM036
     */
    public static final Program QMM036A = new Program(WebAppId.PR, ProgramIdConsts.QMM036A, "QMM036_1",
            "/view/qmm/036/a/index.xhtml");
    public static final Program QMM036B = new Program(WebAppId.PR, ProgramIdConsts.QMM036B, "QMM036_17",
            "/view/qmm/036/b/index.xhtml");
    public static final Program QMM036C = new Program(WebAppId.PR, ProgramIdConsts.QMM036C, "QMM036_18",
            "/view/qmm/036/c/index.xhtml");

    /**
     * QMM040
     */
    public static final Program QMM040A = new Program(WebAppId.PR, ProgramIdConsts.QMM040A, "QMM040_1",
            "/view/qmm/040/a/index.xhtml");

    /**
     * QMM041
     */
    public static final Program QMM041A = new Program(WebAppId.PR, ProgramIdConsts.QMM041A, "QMM041_1",
            "/view/qmm/041/a/index.xhtml");
    public static final Program QMM041B = new Program(WebAppId.PR, ProgramIdConsts.QMM041B, "QMM041_2",
            "/view/qmm/041/b/index.xhtml");
    public static final Program QMM041C = new Program(WebAppId.PR, ProgramIdConsts.QMM041C, "QMM041_3",
            "/view/qmm/041/c/index.xhtml");
    public static final Program QMM041D = new Program(WebAppId.PR, ProgramIdConsts.QMM041D, "QMM041_4",
            "/view/qmm/041/d/index.xhtml");

    /**
     * QMM042
     */
    public static final Program QMM042A = new Program(WebAppId.PR, ProgramIdConsts.QMM042A, "QMM042_1",
            "/view/qmm/042/a/index.xhtml");


    /**
     * QSI001
     */
    public static final Program QSI001A = new Program(WebAppId.PR, ProgramIdConsts.QSI001A, "QSI001_A",
            "/view/qsi/001/a/index.xhtml");
    public static final Program QSI001B = new Program(WebAppId.PR, ProgramIdConsts.QSI001B, "QSI001_B",
            "/view/qsi/001/b/index.xhtml");

    /**
     * QSI002
     */
    public static final Program QSI002A = new Program(WebAppId.PR, ProgramIdConsts.QSI002A, "QSI002_A",
            "/view/qsi/002/a/index.xhtml");
    public static final Program QSI002B = new Program(WebAppId.PR, ProgramIdConsts.QSI002B, "QSI002_B",
            "/view/qsi/002/b/index.xhtml");

    /**
     * QSI003
     */
    public static final Program QSI003A = new Program(WebAppId.PR, ProgramIdConsts.QSI003A, "QSI003_A",
            "/view/qsi/003/a/index.xhtml");
    public static final Program QSI003B = new Program(WebAppId.PR, ProgramIdConsts.QSI003B, "QSI003_B",
            "/view/qsi/003/b/index.xhtml");

    /**
     * QSI013
     */
    public static final Program QSI013A = new Program(WebAppId.PR, ProgramIdConsts.QSI013A, "QSI013_A",
            "/view/qsi/013/a/index.xhtml");
    public static final Program QSI013B = new Program(WebAppId.PR, ProgramIdConsts.QSI013B, "QSI013_B",
            "/view/qsi/013/b/index.xhtml");

    /**
     * QSI014
     */
    public static final Program QSI014A = new Program(WebAppId.PR, ProgramIdConsts.QSI014A, "QSI014_1",
            "/view/qsi/014/a/index.xhtml");
    public static final Program QSI014B = new Program(WebAppId.PR, ProgramIdConsts.QSI014B, "QSI014_2",
            "/view/qsi/014/b/index.xhtml");

    /**
     * QUI001
     */
    public static final Program QUI001A = new Program(WebAppId.PR, ProgramIdConsts.QUI001A, "QUI001_33",
            "/view/qui/001/a/index.xhtml");
    public static final Program QUI001C = new Program(WebAppId.PR, ProgramIdConsts.QUI001C, "QUI001_71",
            "/view/qui/001/c/index.xhtml");

    /**
     * QUI002
     */
    public static final Program QUI002A = new Program(WebAppId.PR, ProgramIdConsts.QUI002A, "QUI002_23",
            "/view/qui/002/a/index.xhtml");
    public static final Program QUI002B = new Program(WebAppId.PR, ProgramIdConsts.QUI002B, "QUI002_15",
            "/view/qui/002/b/index.xhtml");

    /**
     * QUI004
     */
    public static final Program QUI004A = new Program(WebAppId.PR, ProgramIdConsts.QUI004A, "QUI004_33",
            "/view/qui/004/a/index.xhtml");
    public static final Program QUI004C = new Program(WebAppId.PR, ProgramIdConsts.QUI004C, "QUI004_35",
            "/view/qui/004/c/index.xhtml");

    /**
     * KSM010A
     */
    public static final Program KSM010A = new Program(WebAppId.AT, ProgramIdConsts.KSM010A, "KSM010_1",
            "/view/ksm/010/a/index.xhtml");

    /**
     * KSM010B
     */
    public static final Program KSM010B = new Program(WebAppId.AT, ProgramIdConsts.KSM010B, "KSM010_8",
            "/view/ksm/010/b/index.xhtml");

    /**
     * KSM013A
     */
    public static final Program KSM013A = new Program(WebAppId.AT, ProgramIdConsts.KSM013A, "KSM013_1",
            "/view/ksm/013/a/index.xhtml");

    /**
     * KSM015A
     */
    public static final Program KSM015A = new Program(WebAppId.AT, ProgramIdConsts.KSM015A, "KSM015_3",
            "/view/ksm/015/a/index.xhtml");

    /**
     * KSM015B
     */
    public static final Program KSM015B = new Program(WebAppId.AT, ProgramIdConsts.KSM015B, "KSM015_3",
            "/view/ksm/015/b/index.xhtml");
    /**
     * KSM015C
     */
    public static final Program KSM015C = new Program(WebAppId.AT, ProgramIdConsts.KSM015C, "KSM015_6",
            "/view/ksm/015/c/index.xhtml");

    /**
     * KSM015D
     */
    public static final Program KSM015D = new Program(WebAppId.AT, ProgramIdConsts.KSM015D, "KSM015_6",
            "/view/ksm/015/d/index.xhtml");

    /**
     * KDP002
     */
    public static final Program KDP002A = new Program(WebAppId.AT, ProgramIdConsts.KDP002A, "KDP002_1",
            "/view/kdp/002/a/index.xhtml");
    public static final Program KDP002B = new Program(WebAppId.AT, ProgramIdConsts.KDP002B, "KDP002_5",
            "/view/kdp/002/b/index.xhtml");
    public static final Program KDP002C = new Program(WebAppId.AT, ProgramIdConsts.KDP002C, "KDP002_6",
            "/view/kdp/002/c/index.xhtml");
    public static final Program KDP002T = new Program(WebAppId.AT, ProgramIdConsts.KDP002T, "KDP002_23",
            "/view/kdp/002/t/index.xhtml");
    public static final Program KDP002U = new Program(WebAppId.AT, ProgramIdConsts.KDP002U, "KDP002_24",
            "/view/kdp/002/u/index.xhtml");
    public static final Program KDP002L = new Program(WebAppId.AT, ProgramIdConsts.KDP002L, "KDP002_15",
            "/view/kdp/002/l/index.xhtml");


    /**
     * KSM007A
     */
    public static final Program KSM007A = new Program(WebAppId.AT, ProgramIdConsts.KSM007A, "KSM007_1",
            "/view/ksm/007/a/index.xhtml");
    public static final Program KSM007B = new Program(WebAppId.AT, ProgramIdConsts.KSM007B, "KSM007_18",
            "/view/ksm/007/b/index.xhtml");
    public static final Program KSM007C = new Program(WebAppId.AT, ProgramIdConsts.KSM007C, "KSM007_19",
            "/view/ksm/007/c/index.xhtml");
    public static final Program KSM007D = new Program(WebAppId.AT, ProgramIdConsts.KSM007D, "KSM007_20",
            "/view/ksm/007/d/index.xhtml");

    /**
     * KDL047
     */
    public static final Program KDL047 = new Program(WebAppId.AT,
            ProgramIdConsts.KDL047,
            "KDL047_1",
            "/view/kdl/047/a/index.xhtml");
	
	/**
	 * KDL052
	 */
	public static final Program KDL052A = new Program(WebAppId.AT, 
            ProgramIdConsts.KDL052A,
            "KDL052_1",
            "/view/kdl/052/a/index.xhtml");	

	 /**
	 * KDL051
	 */
	public static final Program KDL051 = new Program(WebAppId.AT, 
            ProgramIdConsts.KDL051A,
            "KDL051_1",
            "/view/kdl/051/a/index.xhtml");
	/**
	 * KDL053
	 */
	public static final Program KDL053 = new Program(WebAppId.AT, ProgramIdConsts.KDL053, "KDL053_1",
            "/view/kdl/053/a/index.xhtml");

    /**
     * KDL055
     */
    public static final Program KDL055A = new Program(WebAppId.AT, ProgramIdConsts.KDL055A, "KDL055_1",
            "/view/kdl/055/a/index.xhtml");
    public static final Program KDL055B = new Program(WebAppId.AT, ProgramIdConsts.KDL055B, "KDL055_2",
            "/view/kdl/055/b/index.xhtml");


	/**
	 * KWR003
	 */
	public static final Program KWR003A = new Program(WebAppId.AT,
            ProgramIdConsts.KWR003A,
            "KWR003_100",
            "/view/kwr/003/a/index.xhtml");
	public static final Program KWR003B = new Program(WebAppId.AT,
            ProgramIdConsts.KWR003B,
            "KWR003_200",
            "/view/kwr/003/b/index.xhtml");
	public static final Program KWR003C = new Program(WebAppId.AT,
            ProgramIdConsts.KWR003C,
            "KWR003_300",
            "/view/kwr/003/c/index.xhtml");
	/**
	 * KWR004
	 */
	public static final Program KWR004A = new Program(WebAppId.AT,
            ProgramIdConsts.KWR004A,
            "KWR004_1",
            "/view/kwr/004/a/index.xhtml");
	public static final Program KWR004B = new Program(WebAppId.AT,
            ProgramIdConsts.KWR004B,
            "KWR004_2",
            "/view/kwr/004/b/index.xhtml");
	public static final Program KWR004C = new Program(WebAppId.AT,
            ProgramIdConsts.KWR004C,
            "KWR004_3",
            "/view/kwr/004/c/index.xhtml");
	/**
	 * KWR005
	 */
	public static final Program KWR005A = new Program(WebAppId.AT,
            ProgramIdConsts.KWR005A,
            "KWR005_1",
            "/view/kwr/005/a/index.xhtml");
	public static final Program KWR005B = new Program(WebAppId.AT,
            ProgramIdConsts.KWR005B,
            "KWR005_101",
            "/view/kwr/005/b/index.xhtml");
	public static final Program KWR005C = new Program(WebAppId.AT,
            ProgramIdConsts.KWR005C,
            "KWR005_201",
            "/view/kwr/005/c/index.xhtml");/**

	 * KWR007
	 */
	public static final Program KWR007A = new Program(WebAppId.AT,
            ProgramIdConsts.KWR007A,
            "KWR007_1",
            "/view/kwr/007/a/index.xhtml");
	public static final Program KWR007B = new Program(WebAppId.AT,
            ProgramIdConsts.KWR007B,
            "KWR007_101",
            "/view/kwr/007/b/index.xhtml");
	public static final Program KWR007C = new Program(WebAppId.AT,
            ProgramIdConsts.KWR007C,
            "KWR007_201",
            "/view/kwr/007/c/index.xhtml");

    /**
     * CMM024
     */
    public static final Program CMM024A = new Program(WebAppId.COM, ProgramIdConsts.CMM024A,
            "CMM024_90"
            , "/view/cmm/024/a/index.xhtml");
    public static final Program CMM024B = new Program(WebAppId.COM, ProgramIdConsts.CMM024B,
            "CMM024_91"
            , "/view/cmm/024/b/index.xhtml");
    public static final Program CMM024C = new Program(WebAppId.COM, ProgramIdConsts.CMM024C,
            "CMM024_92"
            , "/view/cmm/024/c/index.xhtml");
    public static final Program CMM024D = new Program(WebAppId.COM, ProgramIdConsts.CMM024D,
            "CMM024_93"
            , "/view/cmm/024/d/index.xhtml");
    public static final Program CMM024F = new Program(WebAppId.COM, ProgramIdConsts.CMM024F,
            "CMM024_94"
            , "/view/cmm/024/f/index.xhtml");

    /**
     * KSM008
     */
    public static final Program KSM008I = new Program(WebAppId.AT, ProgramIdConsts.KSM008I, "KSM008_182",
            "/view/ksm/008/i/index.xhtml");
    public static final Program KSM008K = new Program(WebAppId.AT, ProgramIdConsts.KSM008K, "KSM008_146",
            "/view/ksm/008/k/index.xhtml");
    
    /**
     * KNR001
     */
    public static final Program KNR001A = new Program(WebAppId.AT, ProgramIdConsts.KNR001A, "KNR001_1",
            "/view/knr/001/a/index.xhtml");
    
    /**
     * KNR002
     */
    public static final Program KNR002A = new Program(WebAppId.AT, ProgramIdConsts.KNR002A, "KNR002_1",
            "/view/knr/002/a/index.xhtml");
    public static final Program KNR002B = new Program(WebAppId.AT, ProgramIdConsts.KNR002B, "KNR002_2",
            "/view/knr/002/b/index.xhtml");
    public static final Program KNR002C = new Program(WebAppId.AT, ProgramIdConsts.KNR002C, "KNR002_3",
            "/view/knr/002/c/index.xhtml");
    public static final Program KNR002D = new Program(WebAppId.AT, ProgramIdConsts.KNR002D, "KNR002_4",
            "/view/knr/002/d/index.xhtml");
    public static final Program KNR002E = new Program(WebAppId.AT, ProgramIdConsts.KNR002E, "KNR002_5",
            "/view/knr/002/e/index.xhtml");
    public static final Program KNR002F = new Program(WebAppId.AT, ProgramIdConsts.KNR002F, "KNR002_6",
            "/view/knr/002/f/index.xhtml");
    public static final Program KNR002G = new Program(WebAppId.AT, ProgramIdConsts.KNR002G, "KNR002_7",
            "/view/knr/002/g/index.xhtml");
    public static final Program KNR002H = new Program(WebAppId.AT, ProgramIdConsts.KNR002H, "KNR002_8",
            "/view/knr/002/h/index.xhtml");
    public static final Program KNR002K = new Program(WebAppId.AT, ProgramIdConsts.KNR002K, "KNR002_11",
            "/view/knr/002/k/index.xhtml");
    public static final Program KNR002L = new Program(WebAppId.AT, ProgramIdConsts.KNR002L, "KNR002_12",
    		"/view/knr/002/l/index.xhtml");

    public static final Program KSM008L = new Program(WebAppId.AT, ProgramIdConsts.KSM008L, "KSM008_147",
            "/view/ksm/008/k/index.xhtml");
		
    /**
     * CCG003B
     */
    public static final Program CCG003B = new Program(WebAppId.COM, 
            ProgramIdConsts.CCG003B,
            "CCG003_2",
            "/view/ccg/003/b/index.xhtml");

    /**
     * CCG003C
     */
    public static final Program CCG003C = new Program(WebAppId.COM, 
            ProgramIdConsts.CCG003C,
            "CCG003_3",
            "/view/ccg/003/c/index.xhtml");
    
    /**
     * CCG005B
     */
    public static final Program CCG005B = new Program(WebAppId.COM, 
            ProgramIdConsts.CCG005B,
            "CCG005_28",
            "/view/ccg/005/b/index.xhtml");
    
    /**
     * CCG005D
     */
    public static final Program CCG005D = new Program(WebAppId.COM, 
            ProgramIdConsts.CCG005D,
            "CCG005_29",
            "/view/ccg/005/d/index.xhtml");
    
    /**
     * KTG026
     */
    public static final Program KTG026A = new Program(WebAppId.AT, 
            ProgramIdConsts.KTG026A,
            "KTG026_5",
            "/view/ktg/026/a/superior.xhtml");
    
    /** CDL010 */
    public static final Program CDL010A = new Program(WebAppId.COM, ProgramIdConsts.CDL010A, "CDL010_1",
    		"/view/cdl/010/a/index.xhtml");
    
    public static final Program CDL011A = new Program(WebAppId.COM, ProgramIdConsts.CDL011A, "CDL011_1",
    		"/view/cdl/011/a/index.xhtml");
    
    /**
     * CCGS03
     */
    public static final Program CCGS03A = new Program(WebAppId.MOBI, ProgramIdConsts.CCGS03A, "CCGS03A",
            "/ccg/s03/a");






    /** KWR008C */
    public static final Program KWR008C = new Program(WebAppId.AT
										    		, ProgramIdConsts.KWR008C
										    		, "KWR008_100"
										    		, "/view/kwr/008/c/index.xhtml");
    public static final Program CMM029A = new Program(WebAppId.COM, ProgramIdConsts.CMM029A, "CMM029_1", 
    		"/view/cmm/029/a/index.xhtml");


    /**
     * KHA003
     */
    public static final Program KHA003A = new Program(WebAppId.AT, ProgramIdConsts.KHA003A, "KHA003_1",
            "/view/kha/003/a/index.xhtml");
    public static final Program KHA003B = new Program(WebAppId.AT, ProgramIdConsts.KHA003B, "KHA003_2",
            "/view/kha/003/b/index.xhtml");
    public static final Program KHA003C = new Program(WebAppId.AT, ProgramIdConsts.KHA003C, "KHA003_3",
            "/view/kha/003/c/index.xhtml");
    public static final Program KHA003D = new Program(WebAppId.AT, ProgramIdConsts.KHA003D, "KHA003_4",
            "/view/kha/003/d/index.xhtml");
    public static final Program KHA003E = new Program(WebAppId.AT, ProgramIdConsts.KHA003E, "KHA003_5",
            "/view/kha/003/e/index.xhtml");

    public static final Program OEM001A = new Program(WebAppId.COM, ProgramIdConsts.OEM001A, "OEM001_1",
            "/view/oem/001/a/index.xhtml");
    
    public static final Program OEM002A = new Program(WebAppId.COM, ProgramIdConsts.OEM002A, "OEM002_1",
            "/view/oem/002/a/index.xhtml");
    
    public static final Program OEM002B = new Program(WebAppId.COM, ProgramIdConsts.OEM002B, "OEM002_2",
            "/view/oem/002/b/index.xhtml");
    
    public static final Program OEM004A = new Program(WebAppId.COM, ProgramIdConsts.OEM004A, "OEM004_1",
            "/view/oem/004/a/index.xhtml");
    
    public static final Program OEW001A = new Program(WebAppId.COM, ProgramIdConsts.OEW001A, "OEW001_1",
            "/view/oew/001/a/index.xhtml");
    public static final Program OEW001B = new Program(WebAppId.COM, ProgramIdConsts.OEW001B, "OEW001_2",
            "/view/oew/001/b/index.xhtml");
    public static final Program OEW001C = new Program(WebAppId.COM, ProgramIdConsts.OEW001C, "OEW001_3",
            "/view/oew/001/c/index.xhtml");
    public static final Program OEW001D = new Program(WebAppId.COM, ProgramIdConsts.OEW001D, "OEW001_4",
            "/view/oew/001/d/index.xhtml");
    
    public static final Program OEM003A = new Program(WebAppId.COM, ProgramIdConsts.OEM003A, "OEM003_1",
            "/view/oem/003/a/index.xhtml");
    
    /**
     * All programs map.
     */
    private static final Map<WebAppId, List<Program>> PROGRAMS;

    static {
        Function<Field, Optional<Program>> pg = f -> {
            try {
                if (Modifier.isStatic(f.getModifiers()))
                    return Optional.of((Program) f.get(null));
                else
                    return Optional.empty();
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        };
        PROGRAMS = Arrays.asList(ProgramsManager.class.getFields()).stream().filter(f -> f.getType() == Program.class)
                .map(f -> pg.apply(f).get()).collect(Collectors.groupingBy(Program::getAppId));
    }

    /**
     * Finds program.
     *
     * @param appId appId.
     * @param path  program path.
     * @return optional program.
     */
    public static Optional<Program> find(WebAppId appId, String path) {
        if (appId == null || path == null)
            return Optional.empty();
        Optional<Set<Program>> programsOpt = getSet(appId);
        if (!programsOpt.isPresent())
            return Optional.empty();
        return programsOpt.get().stream().filter(a -> path.equals(a.getPPath())).findFirst();
    }

    /**
     * Finds program by Id.
     *
     * @param appId     appId
     * @param programId programId
     * @return optional program
     */
    public static Optional<Program> findById(WebAppId appId, String programId) {
        if (appId == null || programId == null)
            return Optional.empty();
        Optional<Set<Program>> programsOpt = getSet(appId);
        if (!programsOpt.isPresent())
            return Optional.empty();
        return programsOpt.get().stream().filter(a -> programId.equals(a.getPId())).findFirst();
    }

    /**
     * Finds program by Id.
     *
     * @param programId programId
     * @return optional program
     */
    public static Optional<Program> findById(String programId) {
        return Optional.ofNullable(findById(WebAppId.COM, programId).orElseGet(() ->
                findById(WebAppId.AT, programId).orElseGet(() ->
                        findById(WebAppId.PR, programId).orElse(null)
                )
        ));
    }

    /**
     * Finds program Id.
     *
     * @param appId appId.
     * @param path  path.
     * @return optional program Id.
     */
    public static Optional<String> idOf(WebAppId appId, String path) {
        return Optional.ofNullable(find(appId, path).orElse(new Program()).getPId());
    }

    /**
     * Finds program name.
     *
     * @param appId appId.
     * @param path  path.
     * @return optional program name.
     */
    public static Optional<String> nameOf(WebAppId appId, String path) {
        return Optional.ofNullable(find(appId, path).orElse(new Program()).getPName());
    }

    /**
     * Finds program name by Id.
     *
     * @param appId     appId
     * @param programId programId
     * @return optional program name.
     */
    public static Optional<String> nameById(WebAppId appId, String programId) {
        return Optional.ofNullable(findById(appId, programId).orElse(new Program()).getPName());
    }

    /**
     * Gets predefined set.
     *
     * @param appId appId.
     * @return optional program set.
     */
    private static Optional<Set<Program>> getSet(WebAppId appId) {
        List<Program> programs = PROGRAMS.get(appId);
        if (programs == null || programs.size() == 0)
            return Optional.empty();
        return Optional.of(programs.stream().collect(Collectors.toSet()));
    }
}
