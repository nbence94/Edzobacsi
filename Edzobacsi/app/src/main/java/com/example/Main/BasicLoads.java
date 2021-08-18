package com.example.Main;

import android.content.Context;

import HelperClasses.DatabaseHelper;

public class BasicLoads {

    private DatabaseHelper dh;
    private Context context;

    public BasicLoads(Context context) {
        this.context = context;
        dh = new DatabaseHelper(context);
        loadLevels();
        loadTypes();
        loadMuscles();
        loadTools();
        loadCategory();
        loadExercises();
        loadExerciseContatcts();
        loadRepeats();
        loadIntervals();
    }

    private void loadExercises() {
        String[] names = new String[]  {"Térdelő fekvőtámasz",          //1
                                        "Fekvőtámasz",                  //2
                                        "Gyémánt fekvőtámasz",          //3
                                        "Fekvőtámasz súllyal",          //4

                                        "Ausztrál Húzódzkodás",         //5
                                        "Húzódzkodás",                  //6
                                        "Húzódzkodás súllyal",          //7
                                        "Egy kezes húzódzkodás",        //8

                                        "Pados tolódzkodás",            //9
                                        "Tolódzkodás",                  //10
                                        "Koponyatörés",                 //11

                                        "Guggolás",                     //12
                                        "Falnál tartás",                //13
                                        "Csípőemelés",                  //14
                                        "Kitörés",                      //15
                                        "Egy lábas guggolás",           //16

                                        "Bokaérintés (állva)",         //17
                                        "Bokaérintés (ülve)",          //18
                                        "Pillangó ülés",                //19
                                        "Kobra nyújtás"                 //20
        };

        int[] hand_states = new int[]          {0,0,0,0,0, 0,0,1,0,0, 0,0,0,0,0, 1,0,0,0,0};
        int[] static_states = new int[]        {0,0,0,0,0, 0,0,0,0,0, 0,0,1,0,0, 0,1,1,1,1};
        int[] weight_states = new int[]        {0,0,0,1,0, 0,1,0,0,0, 0,0,0,0,0, 0,0,0,0,0};
        int[] exercise_levels = new int[]         {1,2,3,4,1, 2,3,4,1,2, 3,1,1,1,1, 3,1,1,1,1};
        int[] exercise_types = new int[]          {1,1,1,1,2, 2,2,2,1,1, 1,3,3,3,3, 3,5,5,5,5};

        //id - name - level -static - weight - hand - type
        for(int i = 0; i < names.length; i++) {
            dh.insertExercises(names[i], hand_states[i], static_states[i], weight_states[i], exercise_levels[i], exercise_types[i]);
        }
    }

    void loadExerciseContatcts() {
        int[] exercise_id = new int[]   {1,2,3,4,5, 6,7,8,9,10, 11,12,13,14,15, 16,17,18,19,20};
        int[] category_id = new int[]   {1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,2,2,2,2};
        int[] muscle_exe_id = new int[] {1,2,3,4,5, 6,7,8,9,10, 11,12,13,14,15, 16,17,18,19,20};
        int[] muscle_id = new int[]     {2,2,2,2,8, 8,8,8,5,5, 5,21,21,21,21, 21,12,12,12,12};
        int[] tool_exe_id = new int[]   {1,2,3,4,5, 6,7,8,9,10, 11,12,13,14,15, 16,17,18,19,20};
        int[] tool_id = new int[]       {1,1,1,1,2, 2,2,2,5,4, 4,1,1,3,1,1, 1,1,1,1,1};

        for(int i = 0; i < exercise_id.length; i++) {
            dh.insertConnectTable(dh.EXE_AND_CATE, "categoryID", category_id[i], "exerciseID", exercise_id[i]);
        }

        for(int i = 0; i < muscle_exe_id.length; i++) {
            dh.insertConnectTable(dh.EXE_AND_MG, "exerciseID", muscle_exe_id[i], "musclegroupID", muscle_id[i]);
        }

        for(int i = 0; i < tool_exe_id.length; i++) {
            dh.insertConnectTable(dh.TOOL_AND_EXE, "toolID", tool_id[i], "exerciseID", tool_exe_id[i]);
        }
    }

    private void loadRepeats() {
        int[] type_id_array = new int[] {1,1,1,1,1,1,1,1,1,1, 1,1,1,1,1,2,2,2,2,2,
                                        2,2,2,2,2,2,2,2,2,2, 3,3,3,3,3,3,3,3,3,3,
                                        3,3,3,3,3,4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,
                                        5,5,5,5,5,5,5,5,5,5, 5,5,5,5,5};

        int[] userlevel_array = new int[] {1,2,2,3,3,3,4,4,4,4, 5,5,5,5,5,1,2,2,3,3,
                                            3,4,4,4,4,5,5,5,5,5, 1,2,2,3,3,3,4,4,4,4,
                                            5,5,5,5,5,1,2,2,3,3, 3,4,4,4,4,5,5,5,5,5,
                                            1,2,2,3,3,3,4,4,4,4, 5,5,5,5,5};

        int[] exerciselevel_array = new int[] {1,1,2,1,2,3,1,2,3,4, 1,2,3,4,5,1,1,2,1,2,
                                                3,1,2,3,4,1,2,3,4,5, 1,1,2,1,2,3,1,2,3,4,
                                                1,2,3,4,5,1,1,2,1,2, 3,1,2,3,4,1,2,3,4,5,
                                                1,1,2,1,2,3,1,2,3,4, 1,2,3,4,5};

        int[] reps_amount = new int[] {10,12,10,14,12,10,16,14,12,10, 18,16,14,12,10,4,6,4,8,6,
                                        4,10,8,6,4,12,10,8,6,4,10,15, 10,20,15,10,25,20,15,10,30,25,
                                        20,15,10,16,18,16,20,18,16,22, 20,18,16,24,22,20,18,16,10,15,
                                        10,20,15,10,25,20,15,10,30,25, 20,15,10};

        int[] static_time = new int[] {20,30,20,40,30,20,50,40,30,20, 60,50,40,30,20,10,10,15,10,20,
                                        15,10,25,20,15,10,30,25,20,15, 10,30,35,30,40,35,30,45,40,35,
                                        30,50,45,40,35,30,20,30,20,40, 30,20,50,40,30,20,60,50,40,30,20,
                                        15,25,15,35,25,15,45,35,25,15, 55,45,35,25,15};

        for(int i = 0; i < type_id_array.length; i++) {
            dh.insertRepeats(type_id_array[i],userlevel_array[i],exerciselevel_array[i],reps_amount[i],static_time[i]);
        }
    }

    private void loadIntervals() {
        int[] type_id_array = new int[] {1,1,1,1,1, 2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4, 5,5,5,5,5};
        int[] user_level_array = new int[] {1,2,3,4,5, 1,2,3,4,5, 1,2,3,4,5, 1,2,3,4,5, 1,2,3,4,5};
        int[] reps = new int[] {20,25,35,45,50, 10,15,30,40,50, 20,30,40,50,60, 20,30,40,50,60, 15,20,25,30,35};

        for(int i = 0; i < type_id_array.length; i++) {
            dh.insertIntervals(type_id_array[i],user_level_array[i], reps[i]);
        }
    }

    private void loadLevels() {
        String[] exLevel = new String[] {"Kezdő 1", "Kezdő 2", "Középhaladó 1", "Középhaladó 2", "Haladó 1"};

        for(int i = 0; i < exLevel.length; i++) {
            dh.insert("Name", exLevel[i], dh.LEVELS);
        }
    }

    private void loadTypes() {
        String[] types = new String[] {"Toló gyakorlat", "Húzó gyakorlat", "Láb gyakorlat", "Has gyakorlat", "Tartás"};

        for(int i = 0; i < types.length; i++) {
            dh.insert("Name", types[i], dh.TYPES);
        }
        loadUserLevels(types.length);
    }

    private void loadUserLevels(int length) {
        for(int i = 0; i < length; i++) {
            dh.insert("UserLevel", "1", dh.USERLEVEL);
        }
    }

    private void loadMuscles() {
        String[] muscleGroups = new String[] {
                "Csuklyásizom",             //1
                "Mellizom",                 //2
                "Mellizom (Felső)",         //3
                "Mellizom (Belső)",         //4
                "Hátsó vállizom",           //5
                "Középső vállizom",         //6
                "Elülső vállizom",          //7
                "Bicepsz",                   //8
                "Tricepsz",                     //9
                "Alkar",                        //10
                "Széles hátizom",               //11
                "Mély hátizom",                 //12
                "Egyenes hasizom (Felső)",        //13
                "Egyenes hasizom (Alsó)",           //14
                "Külső hasizom",                    //15
                "Kis farizom",                      //16
                "Közepes farizom",                  //17
                "Nagy farizom",                    //18
                "Combhajlítók",                     //19
                "Külső combizom",                   //20
                "Egyenes combizom" ,                //21
                "Belső combizom",                   //22
                "Vádli"                             //23
        };

        for (String muscleGroup : muscleGroups) {
            dh.insert("Name", muscleGroup, dh.MUSCLE_GROUPS);
        }
    }

    private void loadTools() {

        String[] Tools = new String[] { "Eszköz nélkül",        //1
                                        "Húzódzkodó",          //2
                                        "Húzódzkodó (alacsony)",    //3
                                        "Tolódzkodó",              //4
                                        "Pad",                //5
                                        "Súly",             //6
                                        "Fal"};                //7

        for(int i = 0; i < Tools.length; i++)
        dh.insert("Name", Tools[i], dh.TOOLS);
    }

    private void loadCategory() {
        String[] categories = new String[] {"Erősítés", "Nyújtás"};

        for(int i = 0; i < categories.length; i++) {
            dh.insert("Name", categories[i], dh.CATEGORY);
        }
    }
}
