package com.taskmanager.task;

/**
 * 4 vung cua ma tran Eisenhower.
 * Q1 = Khan cap & Quan trong       -> Do (lam ngay)
 * Q2 = Khong khan cap & Quan trong -> Schedule (len ke hoach)
 * Q3 = Khan cap & Khong quan trong -> Delegate (uy quyen)
 * Q4 = Khong khan cap & Khong quan trong -> Eliminate (loai bo)
 */
public enum Quadrant {
    Q1,
    Q2,
    Q3,
    Q4
}