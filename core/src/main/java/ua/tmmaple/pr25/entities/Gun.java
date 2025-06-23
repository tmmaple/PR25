package ua.tmmaple.pr25.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ua.tmmaple.pr25.audio.Audio;

import java.util.Random;

/**
 * Створює патерни та візерунки куль ворога
 * @author SkyWarp
 * @author uwuhasmile
 */
public class Gun {
    public enum BulletType {
        BULLET_8x8_RED, BULLET_8x8_ORANGE, BULLET_8x8_YELLOW, BULLET_8x8_GREEN, BULLET_8x8_BLUE, BULLET_8x8_PURPLE, BULLET_8x8_WHITE,
        BULLET_4x8_RED, BULLET_4x8_ORANGE, BULLET_4x8_YELLOW,  BULLET_4x8_GREEN, BULLET_4x8_BLUE, BULLET_4x8_PURPLE, BULLET_4x8_WHITE,
        BULLET_12x12_RED, BULLET_12x12_ORANGE, BULLET_12x12_YELLOW, BULLET_12x12_GREEN, BULLET_12x12_BLUE, BULLET_12x12_PURPLE, BULLET_12x12_WHITE,
        BULLET_RINGED_12x12_RED, BULLET_RINGED_12x12_ORANGE, BULLET_RINGED_12x12_YELLOW, BULLET_RINGED_12x12_GREEN, BULLET_RINGED_12x12_BLUE, BULLET_RINGED_12x12_PURPLE, BULLET_RINGED_12x12_WHITE,
        BULLET_8x12_RED, BULLET_8x12_ORANGE, BULLET_8x12_YELLOW, BULLET_8x12_GREEN, BULLET_8x12_BLUE, BULLET_8x12_PURPLE, BULLET_8x12_WHITE,
        BULLET_4x12_RED, BULLET_4x12_ORANGE, BULLET_4x12_YELLOW, BULLET_4x12_GREEN, BULLET_4x12_BLUE, BULLET_4x12_PURPLE, BULLET_4x12_WHITE,
        BULLET_16x16_RED, BULLET_16x16_ORANGE, BULLET_16x16_YELLOW, BULLET_16x16_GREEN, BULLET_16x16_BLUE, BULLET_16x16_PURPLE, BULLET_16x16_WHITE,
        BULLET_RINGED_16x16_RED, BULLET_RINGED_16x16_ORANGE, BULLET_RINGED_16x16_YELLOW, BULLET_RINGED_16x16_GREEN, BULLET_RINGED_16x16_BLUE, BULLET_RINGED_16x16_PURPLE, BULLET_RINGED_16x16_WHITE,
        BULLET_10x16_RED, BULLET_10x16_ORANGE, BULLET_10x16_YELLOW, BULLET_10x16_GREEN, BULLET_10x16_BLUE, BULLET_10x16_PURPLE, BULLET_10x16_WHITE,
        BULLET_32x32_RED, BULLET_32x32_ORANGE, BULLET_32x32_YELLOW, BULLET_32x32_GREEN, BULLET_32x32_BLUE, BULLET_32x32_PURPLE, BULLET_32x32_WHITE,
        BULLET_RINGED_32x32_RED, BULLET_RINGED_32x32_ORANGE, BULLET_RINGED_32x32_YELLOW, BULLET_RINGED_32x32_GREEN, BULLET_RINGED_32x32_BLUE, BULLET_RINGED_32x32_PURPLE, BULLET_RINGED_32x32_WHITE,
        BULLET_16x32_RED, BULLET_16x32_ORANGE, BULLET_16x32_YELLOW, BULLET_16x32_GREEN, BULLET_16x32_BLUE, BULLET_16x32_PURPLE, BULLET_16x32_WHITE
    }

    /**
     * Тип прицілювання gun'а, впливає спавн та інтерпретацію подальших значень.
     * @author uwuhasmile
     */
    public enum Aim {
        /**
         * Створює кулі у формі рівнобічної трапеції, спрямованої товстою стороною в заданому напрямі.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного рядка куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного рядка куль.<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
     *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого рядка куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього рядка куль<br>
         *          Ряди між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - напрям першого стовпчику куль, відносно світу</li>
         *     <li><code>angleB</code> - різниця напрямів між стовпчиками куль</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого рядка куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        FAN_STATIC,
        /**
         * Створює кулі у формі рівнобічної трапеції, спрямованої товстою стороною в напрямі гравця.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного рядка куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного рядка куль.<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
         *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого рядка куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього рядка куль<br>
         *          Ряди між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - напрям першого стовпчику куль, повернений до гравця</li>
         *     <li><code>angleB</code> - різниця напрямів між стовпчиками куль</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого рядка куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        FAN_PLAYER,
        /**
         * Створює кулі у формі рівнобічної трапеції, спрямованої товстою стороною в заданому напрямі, але з випадковим розміщенням по куту.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного рядка куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного рядка куль.<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
         *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого рядка куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього рядка куль<br>
         *          Ряди між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - центральний напрям рядка куль</li>
         *     <li><code>angleB</code> - проміжок кутів відносно <code>angleA</code>, в межах якого випадково створюватимуться кулі.</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого рядка куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RANDOM_FAN_STATIC,
        /**
         * Створює кулі у формі рівнобічної трапеції, спрямованої товстою стороною в заданому напрямі, але з випадковим розміщенням по куту, в сторону гравця.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного рядка куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного рядка куль.<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
         *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого рядка куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього рядка куль<br>
         *          Ряди між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - центральний напрям рядка куль, повернений до гравця</li>
         *     <li><code>angleB</code> - проміжок кутів відносно <code>angleA</code>, в межах якого випадково створюватимуться кулі.</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного рядка куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого рядка куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього рядка куль<br>
         *         Рядки між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RANDOM_FAN_PLAYER,
        /**
         * Створює кулі по колу.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного кільця куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного кільця куль.<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
         *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого кільця куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього кільця куль<br>
         *          Кільця між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - поворот першого кільця куль</li>
         *     <li><code>angleB</code> - Зміщення кута наступних кілець куль після першого</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого кільця куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RING_STATIC,
        /**
         * Створює кулі по колу, яке повернуте до гравця. Якщо <code>offsetMode == PLAYER</code>, то при випуску будуть спрямовані на гравця<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - швидкість першого випущеного кільця куль</li>
         *     <li>
         *         <code>speedB</code> - швидкість останнього випущеного кільця куль.<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>speedA</code> та <code>speedB</code>
         *          </li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого кільця куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього кільця куль<br>
         *          Кільця між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - поворот першого кільця куль, повернений до гравця</li>
         *     <li><code>angleB</code> - Зміщення кута наступних кілець куль після першого</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого кільця куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RING_PLAYER,
        /**
         * Створює кулі по колу з випадковими швидкостями.<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - мінімальна випадкова швидкість кулі</li>
         *     <li><code>speedB</code> - максимальна випадкова швидкість кулі</li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого кільця куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього кільця куль<br>
         *          Кільця між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - поворот першого кільця куль</li>
         *     <li><code>angleB</code> - Зміщення кута наступних кілець куль після першого</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого кільця куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RANDOM_RING_STATIC,
        /**
         * Створює кулі по колу з випадковими швидкостями, повернуте до гравця. Якщо <code>offsetMode == PLAYER</code>, то при випуску будуть спрямовані на гравця<br>
         * Інтерпретація значень:
         * <ul>
         *     <li><code>countA</code> - кількість стовпців куль</li>
         *     <li><code>countB</code> - скільки рядків куль</li>
         *     <li><code>speedA</code> - мінімальна випадкова швидкість кулі</li>
         *     <li><code>speedB</code> - максимальна випадкова швидкість кулі</li>
         *     <li><code>accelerationA</code> - лінійне прискорення першого випущеого кільця куль</li>
         *     <li>
         *         <code>accelerationB</code> - лінійне прискорення першого останнього кільця куль<br>
         *          Кільця між першим та останнім використовують проміжні значення між <code>accelerationA</code> та <code>accelerationB</code>
         *     </li>
         *     <li><code>angleA</code> - поворот першого кільця куль, спрямований на гравця</li>
         *     <li><code>angleB</code> - Зміщення кута наступних кілець куль після першого</li>
         *     <li><code>angularSpeedA</code> - швидкість повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularSpeedB</code> - швидкість повороту першого випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularSpeedA</code> та <code>angularSpeedB</code>
         *     </li>
         *     <li><code>angularAccelerationA</code> - прискорення повороту першого випущеного кільця куль</li>
         *     <li>
         *         <code>angularAccelerationB</code> - прискорення повороту останнього випущеного кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>angularAccelerationA</code> та <code>angularAccelerationB</code>
         *     </li>
         *     <li><code>radiusA</code> - радіус спавну першого кільця куль</li>
         *     <li>
         *         <code>radiusB</code> - радіус спавну останнього кільця куль<br>
         *         Кільця між першим та останнім використовують проміжні значення між <code>radiusA</code> та <code>radiusB</code>
         *     </li>
         * </ul>
         */
        RANDOM_RING_PLAYER,
    }

    /**
     * Типи позиціонування gun.
     * @author uwuhasmile
     */
    public enum OffsetMode {
        /** Створює кулі відносно позиції цього ворога. **/
        ENEMY,
        /** Створює кулі відносно центру згори ігрового поля. **/
        ABSOLUTE,
        /** Створює кулі відносно позиції гравця. **/
        PLAYER,
    }

    private final Enemy owner;

    public String fireSound;
    public BulletType bulletType;
    public Aim aim;

    public OffsetMode offsetMode;
    public final Vector2 offset;

    public int countA;
    public int countB;
    public float speedA;
    public float speedB;
    public float accelerationA;
    public float accelerationB;
    public float angleA;
    public float angleB;
    public float angularSpeedA;
    public float angularSpeedB;
    public float angularAccelerationA;
    public float angularAccelerationB;
    public float radiusA;
    public float radiusB;

    public short repeat;
    public short interval;
    public short delay;

    private boolean on;
    private short repeatsLeft;
    private short currentInterval;

    private final Array<BulletManager.EnemyBullet> ownedBullets;
    private final Random random;

    public Gun(Enemy owner) {
        this.owner = owner;
        offset = new Vector2();
        ownedBullets =  new Array<>(128);
        ownedBullets.ordered = false;
        random = new Random();
        init();
    }

    /**
     * Ініціалізує до початкових параметрів.
     * @author uwuhasmile
     */
    public void init() {
        fireSound = null;
        offsetMode = OffsetMode.ENEMY;
        bulletType = BulletType.BULLET_16x16_RED;
        aim = Aim.FAN_PLAYER;
        countA = 0;
        countB = 0;
        speedA = 0.0f;
        speedB = 0.0f;
        accelerationA = 0.0f;
        accelerationB = 0.0f;
        angleA = 0.0f;
        angleB = 0.0f;
        angularSpeedA = 0.0f;
        angularSpeedB = 0.0f;
        angularAccelerationA = 0.0f;
        angularAccelerationB = 0.0f;
        radiusA = 0.0f;
        radiusB = 0.0f;
        repeat = 1;
        interval = 0;
        delay = 0;
    }

    /**
     * Починає стрільбу.
     * @author uwuhasmile
     */
    public void start() {
        on = true;
        repeatsLeft = repeat;
        currentInterval = delay;
    }

    /**
     * Зупиняє стрільбу.
     * @author uwuhasmile
     */
    public void stop() {
        on = false;
    }

    /**
     * Оновлює стан таймерів та стріляє, якщо таймери закінчились.
     * @author uwuhasmile
     */
    public void update() {
        for (int i = 0; i < ownedBullets.size; ++i) {
            if (!ownedBullets.get(i).active)
                ownedBullets.removeIndex(i);
        }
        if (!on) return;
        if (currentInterval == 0) {
            if (repeat == 0 || repeatsLeft > 0) {
                currentInterval = interval;
                --repeatsLeft;
            }
            fire();
            if (repeatsLeft == 0)
                on = false;
        } else
            --currentInterval;
    }

    /**
     * Спрямовує випущені кулі на гравця.
     * @author uwuhasmile
     */
    public void adjustAimAtPlayer(Vector2 offset) {
        Vector2 position = offset.cpy().add(Player.global.position);
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setAngle(MathUtils.atan2(position.y - bullet.position.y, position.x - bullet.position.x));
    }

    /**
     * Спрямовує випущені кулі на <code>position</code>.
     * @author uwuhasmile
     */
    public void adjustAimAt(Vector2 position) {
        Vector2 absolutePosition = position.cpy().add(GameplayManager.VIEWPORT_START_X + GameplayManager.VIEWPORT_WIDTH * 0.5f, GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT);
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setAngle(MathUtils.atan2(position.y - bullet.position.y, position.x - bullet.position.x));
    }

    /**
     * Змінює швидкість випущених куль в полярних координатах.
     * @author uwuhasmile
     */
    public void adjustVelocity(float angle, float speed) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setVelocity(angle, speed);
    }

    /**
     * Змінює лінійну швидкість випущених куль.
     * @author uwuhasmile
     */
    public void adjustSpeed(float speed) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setSpeed(speed);
    }

    /**
     * Змінює лінійне прискорення випущених куль.
     * @author uwuhasmile
     */
    public void adjustAcceleration(float acceleration) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.acceleration = acceleration;
    }

    /**
     * Змінює напрям випущених куль.
     * @author uwuhasmile
     */
    public void adjustAngle(float angle) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setAngle(angle);
    }

    /**
     * Змінює швидкість повороту випущених куль.
     * @author uwuhasmile
     */
    public void adjustAngularSpeed(float angularSpeed) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.setAngularSpeed(angularSpeed);
    }

    /**
     * Змінює прискорення повороту випущених куль.
     * @author uwuhasmile
     */
    public void adjustAngularAcceleration(float angularAcceleration) {
        for (BulletManager.Bullet bullet : ownedBullets)
            bullet.angularAcceleration = angularAcceleration;
    }

    /**
     * Змінює тип випущених куль.
     * @author uwuhasmile
     */
    public void adjustType(BulletType bulletType) {
        for (BulletManager.EnemyBullet bullet : ownedBullets)
            bullet.setType(BulletManager.BULLET_TYPES[bulletType.ordinal()]);
    }

    /**
     * Знищує всі кулі.
     * @param vfx чи програвати візуальні ефекти.
     * @author uwuhasmile
     */
    public void destroyAll(boolean vfx) {
        for (BulletManager.Bullet bullet : ownedBullets) {
            if (vfx)
                VfxManager.global.spawnDust(bullet.position, bullet.getAngle(), 12, 0.0f, 4.0f);
            bullet.toPool();
        }
        ownedBullets.clear();
    }

    /**
     * Стріляє кулями в певному патерні на основі заданих раніше параметрів.
     * @author uwuhasmile
     */
    private void fire() {
        Vector2 absolutePosition = offset.cpy().add(GameplayManager.VIEWPORT_START_X +GameplayManager.VIEWPORT_WIDTH * 0.5f, GameplayManager.VIEWPORT_START_Y + GameplayManager.VIEWPORT_HEIGHT);
        switch (offsetMode) {
            case ENEMY:
                absolutePosition.add(owner.absolutePosition());
                break;
            case PLAYER:
                absolutePosition.add(Player.global.position);
                break;
        }
        int bulletType = this.bulletType.ordinal();
        float angle = 0.0f;
        if (fireSound != null)
            Audio.global.playSound(fireSound, 1.0f);
        switch (aim) {
            case FAN_PLAYER:
                angle = MathUtils.atan2(Player.global.position.y - absolutePosition.y, Player.global.position.x - absolutePosition.x);
            case FAN_STATIC: {
                angle += angleA;
                float start = angle - angleB * MathUtils.floor(countA * 0.5f) - angleB * (countA % 2 - 1) * 0.5f;
                float speedStep = (speedB - speedA) / countB;
                float radiusStep = (radiusB - radiusA) / countB;
                float accelerationStep = (accelerationB - accelerationA) / countB;
                float angularSpeedStep = (angularSpeedB - angularSpeedA) / countB;
                float angularAccelerationStep = (angularAccelerationB - angularAccelerationA) / countB;
                for (int i = 0; i < countB; ++i) {
                    for (int j = 0; j < countA; ++j) {
                        float bulletAngle = start + angleB * j;
                        float bulletRadius = radiusA + radiusStep * i;
                        Vector2 bulletPosition = absolutePosition.cpy().add(MathUtils.cos(bulletAngle) * bulletRadius, MathUtils.sin(bulletAngle) * bulletRadius);
                        if (aim == Aim.FAN_PLAYER && offsetMode == OffsetMode.PLAYER)
                            bulletAngle = MathUtils.atan2(Player.global.position.y - bulletPosition.y, Player.global.position.x - bulletPosition.x);
                        BulletManager.EnemyBullet bullet = BulletManager.global.createEnemyBullet(bulletPosition,
                            bulletAngle,
                            speedA + speedStep * i,
                            accelerationA + accelerationStep * j,
                            angularSpeedA + angularSpeedStep * j,
                            angularAccelerationA + angularAccelerationStep * j,
                            bulletType
                        );
                        if (bullet != null)
                            ownedBullets.add(bullet);
                    }
                }
            } break;
            case RANDOM_FAN_PLAYER:
                angle = MathUtils.atan2(Player.global.position.y - absolutePosition.y, Player.global.position.x - absolutePosition.x);
            case RANDOM_FAN_STATIC: {
                angle += angleA;
                float speedStep = (speedB - speedA) / countB;
                float radiusStep = (radiusB - radiusA) / countB;
                float accelerationStep = (accelerationB - accelerationA) / countB;
                float angularSpeedStep = (angularSpeedB - angularSpeedA) / countB;
                float angularAccelerationStep = (angularAccelerationB - angularAccelerationA) / countB;
                for (int i = 0; i < countB; ++i) {
                    for (int j = 0; j < countA; ++j) {
                        float bulletAngle = angle + random.nextFloat(-angleB * 0.5f, angleB * 0.5f);
                        float bulletRadius = radiusA + radiusStep * i;
                        Vector2 bulletPosition = absolutePosition.cpy().add(MathUtils.cos(bulletAngle) * bulletRadius, MathUtils.sin(bulletAngle) * bulletRadius);
                        if (aim == Aim.RANDOM_FAN_PLAYER && offsetMode == OffsetMode.PLAYER)
                            bulletAngle = MathUtils.atan2(Player.global.position.y - bulletPosition.y, Player.global.position.x - bulletPosition.x);
                        BulletManager.EnemyBullet bullet = BulletManager.global.createEnemyBullet(bulletPosition,
                            bulletAngle,
                            speedA + speedStep * i,
                            accelerationA + accelerationStep * j,
                            angularSpeedA + angularSpeedStep * j,
                            angularAccelerationA + angularAccelerationStep * j,
                            bulletType
                        );
                        if (bullet != null)
                            ownedBullets.add(bullet);
                    }
                }
            } break;
            case RING_PLAYER:
                angle = MathUtils.atan2(Player.global.position.y - absolutePosition.y, Player.global.position.x - absolutePosition.x);
            case RING_STATIC: {
                angle += angleA;
                float start = angle - angleB * MathUtils.floor(countA * 0.5f) - angleB * (countA % 2 - 1) * 0.5f;
                float angleStep = MathUtils.PI2 / countA;
                float speedStep = (speedB - speedA) / countB;
                float radiusStep = (radiusB - radiusA) / countB;
                float accelerationStep = (accelerationB - accelerationA) / countB;
                float angularSpeedStep = (angularSpeedB - angularSpeedA) / countB;
                float angularAccelerationStep = (angularAccelerationB - angularAccelerationA) / countB;
                for (int i = 0; i < countB; ++i) {
                    for (int j = 0; j < countA; ++j) {
                        float bulletAngle = start + angleStep * j + angleB * i;
                        float bulletRadius = radiusA + radiusStep * i;
                        Vector2 bulletPosition = absolutePosition.cpy().add(MathUtils.cos(bulletAngle) * bulletRadius, MathUtils.sin(bulletAngle) * bulletRadius);
                        if (aim == Aim.RING_PLAYER && offsetMode == OffsetMode.PLAYER)
                            bulletAngle = MathUtils.atan2(Player.global.position.y - bulletPosition.y, Player.global.position.x - bulletPosition.x);
                        BulletManager.EnemyBullet bullet = BulletManager.global.createEnemyBullet(bulletPosition,
                            bulletAngle,
                            speedA + speedStep * i,
                            accelerationA + accelerationStep * j,
                            angularSpeedA + angularSpeedStep * j,
                            angularAccelerationA + angularAccelerationStep * j,
                            bulletType
                        );
                        if (bullet != null)
                            ownedBullets.add(bullet);
                    }
                }
            } break;
            case RANDOM_RING_PLAYER:
                angle = MathUtils.atan2(Player.global.position.y - absolutePosition.y, Player.global.position.x - absolutePosition.x);
            case RANDOM_RING_STATIC: {
                angle += angleA;
                float angleStep = MathUtils.PI2 / countA;
                float radiusStep = (radiusB - radiusA) / countB;
                float speedA = Math.min(this.speedA, this.speedB);
                float speedB = Math.max(this.speedA, this.speedB);
                float accelerationStep = (accelerationB - accelerationA) / countB;
                float angularSpeedStep = (angularSpeedB - angularSpeedA) / countB;
                float angularAccelerationStep = (angularAccelerationB - angularAccelerationA) / countB;
                for (int i = 0; i < countB; ++i) {
                    for (int j = 0; j < countA; ++j) {
                        float bulletAngle = angle + angleStep * j + angleB * i;
                        float bulletRadius = radiusA + radiusStep * i;
                        Vector2 bulletPosition = absolutePosition.cpy().add(MathUtils.cos(bulletAngle) * bulletRadius, MathUtils.sin(bulletAngle) * bulletRadius);
                        if (aim == Aim.RANDOM_RING_PLAYER && offsetMode == OffsetMode.PLAYER)
                            bulletAngle = MathUtils.atan2(Player.global.position.y - bulletPosition.y, Player.global.position.x - bulletPosition.x);
                        BulletManager.EnemyBullet bullet = BulletManager.global.createEnemyBullet(bulletPosition,
                            bulletAngle,
                            random.nextFloat(speedA, speedB),
                            accelerationA + accelerationStep * j,
                            angularSpeedA + angularSpeedStep * j,
                            angularAccelerationA + angularAccelerationStep * j,
                            bulletType
                        );
                        if (bullet != null)
                            ownedBullets.add(bullet);
                    }
                }
            } break;
        }
    }
}
