package com.apple.team_prometheus.domain.auth.entity

import com.apple.team_prometheus.domain.attendance.entity.Attendance
import com.apple.team_prometheus.domain.attendance.entity.NoAttendance
import com.apple.team_prometheus.domain.going.entity.GoingApply
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate
import java.time.Year

@Entity(name = "users")
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "unique_name_birth_year",
            columnNames = ["name", "birth_year"]
        )
    ]
)
data class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    @JsonIgnore
    var password: String,

    @Column(nullable = false, name = "name")
    var name: String,

    var roomNum: Int? = null,

    @Column(nullable = false)
    val role: Role,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    var attendance: Attendance? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    var noAttendance: List<NoAttendance>,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    var goingApply: List<GoingApply> = emptyList(),

    @Column(nullable = false, name = "birth_year")
    var birth: LocalDate,

    @Column(nullable = false)
    val yearOfAdmission: Year,

    @Column(nullable = false)
    var isGraduate: Boolean,

) {
    constructor() : this(
        password = "",
        name = "",
        roomNum = 0,
        role = Role.STUDENT,
        attendance = null,
        noAttendance = emptyList(),
        goingApply = emptyList(),
        birth = LocalDate.now(),
        yearOfAdmission = Year.now(),
        isGraduate = false
    )

}