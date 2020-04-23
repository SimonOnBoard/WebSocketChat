package ru.itis.websockets.orm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private String id;

    private String name;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "chat")
    private List<PageUser> users;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String code;
}
