package com.wealthwise.unit.domain.category;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Category - Aggregate Root")
class CategoryTest {

    @Nested
    @DisplayName("Création")
    class Creation {

        @Test
        @DisplayName("doit créer une catégorie de dépense")
        void shouldCreateExpenseCategory() {
            Category cat = Category.create("Alimentation", CategoryType.EXPENSE, "#FF5733", "shopping-cart");

            assertThat(cat.getId()).isNotNull();
            assertThat(cat.getName()).isEqualTo("Alimentation");
            assertThat(cat.getType()).isEqualTo(CategoryType.EXPENSE);
            assertThat(cat.getColor()).isEqualTo("#FF5733");
            assertThat(cat.getIcon()).isEqualTo("shopping-cart");
        }

        @Test
        @DisplayName("doit créer une catégorie de revenu")
        void shouldCreateIncomeCategory() {
            Category cat = Category.create("Salaire", CategoryType.INCOME, "#33FF57", null);

            assertThat(cat.getType()).isEqualTo(CategoryType.INCOME);
            assertThat(cat.getIcon()).isNull();
        }

        @Test
        @DisplayName("doit rejeter un nom vide")
        void shouldRejectBlankName() {
            assertThatThrownBy(() -> Category.create("", CategoryType.EXPENSE, "#FF5733", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");

            assertThatThrownBy(() -> Category.create(null, CategoryType.EXPENSE, "#FF5733", null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("doit rejeter un type null")
        void shouldRejectNullType() {
            assertThatThrownBy(() -> Category.create("Test", null, "#FF5733", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CategoryType");
        }

        @Test
        @DisplayName("doit rejeter une couleur invalide")
        void shouldRejectInvalidColor() {
            assertThatThrownBy(() -> Category.create("Test", CategoryType.EXPENSE, "red", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hex");

            assertThatThrownBy(() -> Category.create("Test", CategoryType.EXPENSE, "#GGG", null))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> Category.create("Test", CategoryType.EXPENSE, null, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Modification")
    class Modification {

        @Test
        @DisplayName("doit renommer la catégorie")
        void shouldRename() {
            Category cat = Category.create("Alimentaiton", CategoryType.EXPENSE, "#FF5733", null);

            cat.rename("Alimentation");

            assertThat(cat.getName()).isEqualTo("Alimentation");
        }

        @Test
        @DisplayName("doit rejeter un renommage avec un nom vide")
        void shouldRejectBlankRename() {
            Category cat = Category.create("Test", CategoryType.EXPENSE, "#FF5733", null);

            assertThatThrownBy(() -> cat.rename(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("doit changer la couleur")
        void shouldChangeColor() {
            Category cat = Category.create("Test", CategoryType.EXPENSE, "#FF5733", null);

            cat.changeColor("#00AABB");

            assertThat(cat.getColor()).isEqualTo("#00AABB");
        }

        @Test
        @DisplayName("doit rejeter une couleur invalide au changement")
        void shouldRejectInvalidColorChange() {
            Category cat = Category.create("Test", CategoryType.EXPENSE, "#FF5733", null);

            assertThatThrownBy(() -> cat.changeColor("bleu"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("doit changer l'icône")
        void shouldChangeIcon() {
            Category cat = Category.create("Test", CategoryType.EXPENSE, "#FF5733", null);

            cat.changeIcon("utensils");

            assertThat(cat.getIcon()).isEqualTo("utensils");
        }
    }
}
