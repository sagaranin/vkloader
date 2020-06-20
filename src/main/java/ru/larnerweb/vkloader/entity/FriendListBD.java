package ru.larnerweb.vkloader.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Objects;


@Entity
@Table(name = "friends_bidirectional")
@TypeDefs({@TypeDef(name = "int-array", typeClass = IntArrayType.class)})
public class FriendListBD {
    @Id
    private int id;

    @Type(type = "int-array")
    @Column(name = "friends", columnDefinition = "integer[]")
    private int[] friends;

    public FriendListBD() {
    }

    public FriendListBD(int id, int[] friends) {
        this.id = id;
        this.friends = friends;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getFriends() {
        return friends;
    }

    public void setFriends(int[]friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendListBD that = (FriendListBD) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FriendList{" +
                "id=" + id +
                ", friends=" + Arrays.toString(friends) +
                '}';
    }
}
