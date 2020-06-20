package ru.larnerweb.vkloader.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "friends")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class FriendList {
    @Id
    private int id;

    @Type(type = "list-array")
    @Column(name = "friends", columnDefinition = "integer[]")
    private List<Integer> friends;

    public FriendList() {
    }

    public FriendList(int id, List<Integer>  friends) {
        this.id = id;
        this.friends = friends;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer>  getFriends() {
        return friends;
    }

    public void setFriends(List<Integer>  friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendList that = (FriendList) o;
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
                ", friends=" + friends +
                '}';
    }
}
