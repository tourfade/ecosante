package com.kamitsoft.ecosante.constant;

import com.kamitsoft.ecosante.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public enum NavMenuConstant {
    NAV_USER_HOME(0,false,0,R.id.nav_home, R.string.menu_home),
    NAV_USER_APPOINTMENTS(1,true,1,R.id.nav_user_appointments, R.string.myappointments),
    NAV_USER_PATIENTS(2,true,1,R.id.nav_user_patients, R.string.mypatients),
    NAV_USER_VISITS(3,true, 1,R.id.nav_user_visits, R.string.myencounters),
    NAV_NURSE_SUPERVISORS(4, true, 2, R.id.nav_supervisors, R.string.supervisors),

    NAV_SUPERVISED_NURSES(4,true, 3,R.id.nav_supervised_nurses, R.string.supervised_nurses),
    NAV_SUPERVISED_VISITS(5,true, 3,R.id.nav_supervised_visits, R.string.supervised_visits),
    NAV_SUPERVISED_PATIENTS(6,true, 3,R.id.nav_supervised_patients, R.string.supervise_patients),

    NAV_ADMIN_DISTRICTS(7, true, 4,R.id.nav_my_districs, R.string.districts),
    NAV_ADMIN_PHYSICIANS(8,true, 4,R.id.nav_admin_physicians, R.string.physiscians),
    NAV_ADMIN_NURSES(9,true, 4,R.id.nav_admin_nurses, R.string.nurses),
    NAV_USER_PROFILE(10,false,0,R.id.nav_profile, R.string.profile),
    NAV_USER_DISCONNECT(11, false,0,R.id.nav_disconnect, R.string.disconnect),
    NAV_USER_STATUS(12,false,1,R.id.nav_available, R.string.available);//-1

    public final @IdRes int id;
    public final @StringRes int name;
    public final int index;
    public final int level;
    public final boolean isList;
    public  int count = 0;
    NavMenuConstant(int index,boolean isList,int level, @IdRes int id, @StringRes int name){
        this.index = index;
        this.id = id;
        this.name = name;
        this.level = level;
        this.isList = isList;
    }

    public  static List<NavMenuConstant> filter(Predicate<NavMenuConstant> p){
        List<NavMenuConstant> navs = new ArrayList<>();
        for (NavMenuConstant n: values()){
            if(p.test(n)){
                navs.add(n);
            }
        }
        return navs;
    }
    // 0 all, 1-nurse-phys, 2: nurse onlys, 3 phys only, 4 admin only
    public  boolean isAuthorized(int userType){
        switch (level){
            case 0:return true;
            case 1: return UserType.isNurse(userType) || UserType.isPhysist(userType);
            case 2: return UserType.isNurse(userType);
            case 3: return UserType.isPhysist(userType);
            case 4: return UserType.isAdmin(userType);
            default:return false;

        }
    }



    public static NavMenuConstant ofMenu(int id){
        for (NavMenuConstant s: values()) {
            if (s.id == id){
                return s;
            }
        }
        return null;
    }
}
