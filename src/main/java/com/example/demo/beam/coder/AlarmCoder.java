package com.example.demo.beam.coder;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.*;
import org.apache.avro.reflect.*;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.ClassUtils;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.util.EmptyOnDeserializationThreadLocal;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Supplier;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Suppliers;

import javax.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/5/10 17:41
 */
public class AlarmCoder<T> extends CustomCoder<T> {

    @Override
    public void encode(T value, OutputStream outStream) throws CoderException, IOException {
        // Get a BinaryEncoder instance from the ThreadLocal cache and attempt to reuse it.
        BinaryEncoder encoderInstance = ENCODER_FACTORY.directBinaryEncoder(outStream, encoder.get());
        // Save the potentially-new instance for reuse later.
        encoder.set(encoderInstance);
        writer.get().write(value, encoderInstance);
        // Direct binary encoder does not buffer any data and need not be flushed.
    }

    @Override
    public T decode(InputStream inStream) throws CoderException, IOException {
        // Get a BinaryDecoder instance from the ThreadLocal cache and attempt to reuse it.
        BinaryDecoder decoderInstance = DECODER_FACTORY.directBinaryDecoder(inStream, decoder.get());
        // Save the potentially-new instance for later.
        decoder.set(decoderInstance);
        return reader.get().read(null, decoderInstance);
    }

    /**
     * Returns an {@code AvroCoder} instance for the provided element type.
     *
     * @param <T> the element type
     */
    public static <T> AlarmCoder<T> of(TypeDescriptor<T> type) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) type.getRawType();
        return of(clazz);
    }

    /**
     * Returns an {@code AvroCoder} instance for the provided element class.
     *
     * @param <T> the element type
     */
    public static <T> AlarmCoder<T> of(Class<T> clazz) {
        return new AlarmCoder<>(clazz, new ReflectData(clazz.getClassLoader()).getSchema(clazz));
    }


    /**
     * Returns an {@code AvroCoder} instance for the Avro schema. The implicit type is GenericRecord.
     */
    public static AlarmCoder<GenericRecord> of(Schema schema) {
        return new AlarmCoder<>(GenericRecord.class, schema);
    }

    /**
     * Returns an {@code AvroCoder} instance for the provided element type using the provided Avro
     * schema.
     *
     * <p>If the type argument is GenericRecord, the schema may be arbitrary. Otherwise, the schema
     * must correspond to the type provided.
     *
     * @param <T> the element type
     */
    public static <T> AlarmCoder<T> of(Class<T> type, Schema schema) {
        return new AlarmCoder<>(type, schema);
    }

    /**
     * Returns a {@link CoderProvider} which uses the {@link AvroCoder} if possible for all types.
     *
     * <p>It is unsafe to register this as a {@link CoderProvider} because Avro will reflectively
     * accept dangerous types such as {@link Object}.
     *
     * <p>This method is invoked reflectively from {@link DefaultCoder}.
     */
    @SuppressWarnings("unused")
    public static CoderProvider getCoderProvider() {
        return new AlarmCoderProvider();
    }

    /**
     * A {@link CoderProvider} that constructs an {@link AvroCoder} for Avro compatible classes.
     *
     * <p>It is unsafe to register this as a {@link CoderProvider} because Avro will reflectively
     * accept dangerous types such as {@link Object}.
     */
    static class AlarmCoderProvider extends CoderProvider {
        @Override
        public <T> Coder<T> coderFor(
                TypeDescriptor<T> typeDescriptor, List<? extends Coder<?>> componentCoders)
                throws CannotProvideCoderException {
            try {
                return AlarmCoder.of(typeDescriptor);
            } catch (AvroRuntimeException e) {
                throw new CannotProvideCoderException(
                        String.format("%s is not compatible with Avro", typeDescriptor), e);
            }
        }
    }

    private final Class<T> type;
    private final SerializableSchemaSupplier schemaSupplier;
    private final TypeDescriptor<T> typeDescriptor;

    private final List<String> nonDeterministicReasons;

    // Factories allocated by .get() are thread-safe and immutable.
    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();
    private static final DecoderFactory DECODER_FACTORY = DecoderFactory.get();

    /**
     * A {@link Serializable} object that holds the {@link String} version of a {@link Schema}. This
     * is paired with the {@link SerializableSchemaSupplier} via {@link Serializable}'s usage of the
     * {@link #readResolve} method.
     */
    private static class SerializableSchemaString implements Serializable {
        private final String schema;

        private SerializableSchemaString(String schema) {
            this.schema = schema;
        }

        private Object readResolve() throws IOException, ClassNotFoundException {
            return new SerializableSchemaSupplier(new Schema.Parser().parse(schema));
        }
    }

    /**
     * A {@link Serializable} object that delegates to the {@link SerializableSchemaString} via {@link
     * Serializable}'s usage of the {@link #writeReplace} method. Kryo doesn't utilize Java's
     * serialization and hence is able to encode the {@link Schema} object directly.
     */
    private static class SerializableSchemaSupplier implements Serializable, Supplier<Schema> {
        private final Schema schema;

        private SerializableSchemaSupplier(Schema schema) {
            this.schema = schema;
        }

        private Object writeReplace() {
            return new SerializableSchemaString(schema.toString());
        }

        @Override
        public Schema get() {
            return schema;
        }
    }

    /**
     * A {@link Serializable} object that lazily supplies a {@link ReflectData} built from the
     * appropriate {@link ClassLoader} for the type encoded by this {@link AvroCoder}.
     */
    private static class SerializableReflectDataSupplier
            implements Serializable, Supplier<ReflectData> {

        private final Class<?> clazz;

        private SerializableReflectDataSupplier(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public ReflectData get() {
            return new ReflectData(clazz.getClassLoader());
        }
    }

    // Cache the old encoder/decoder and let the factories reuse them when possible. To be threadsafe,
    // these are ThreadLocal. This code does not need to be re-entrant as AvroCoder does not use
    // an inner coder.
    private final EmptyOnDeserializationThreadLocal<BinaryDecoder> decoder;
    private final EmptyOnDeserializationThreadLocal<BinaryEncoder> encoder;
    private final EmptyOnDeserializationThreadLocal<DatumWriter<T>> writer;
    private final EmptyOnDeserializationThreadLocal<DatumReader<T>> reader;

    // Lazily re-instantiated after deserialization
    private final Supplier<ReflectData> reflectData;

    protected AlarmCoder(Class<T> type, Schema schema) {
        this.type = type;
        this.schemaSupplier = new SerializableSchemaSupplier(schema);
        typeDescriptor = TypeDescriptor.of(type);
        nonDeterministicReasons = new AlarmDeterminismChecker().check(TypeDescriptor.of(type), schema);

        // Decoder and Encoder start off null for each thread. They are allocated and potentially
        // reused inside encode/decode.
        this.decoder = new EmptyOnDeserializationThreadLocal<>();
        this.encoder = new EmptyOnDeserializationThreadLocal<>();

        this.reflectData = Suppliers.memoize(new SerializableReflectDataSupplier(getType()));

        // Reader and writer are allocated once per thread per Coder
        this.reader =
                new EmptyOnDeserializationThreadLocal<DatumReader<T>>() {
                    private final AlarmCoder<T> myCoder = AlarmCoder.this;

                    @Override
                    public DatumReader<T> initialValue() {
                        return myCoder.getType().equals(GenericRecord.class)
                                ? new GenericDatumReader<>(myCoder.getSchema())
                                : new ReflectDatumReader<>(
                                myCoder.getSchema(), myCoder.getSchema(), myCoder.reflectData.get());
                    }
                };

        this.writer =
                new EmptyOnDeserializationThreadLocal<DatumWriter<T>>() {
                    private final AlarmCoder<T> myCoder = AlarmCoder.this;

                    @Override
                    public DatumWriter<T> initialValue() {
                        return myCoder.getType().equals(GenericRecord.class)
                                ? new GenericDatumWriter<>(myCoder.getSchema())
                                : new ReflectDatumWriter<>(myCoder.getSchema(), myCoder.reflectData.get());
                    }
                };
    }

    /** Returns the type this coder encodes/decodes. */
    public Class<T> getType() {
        return type;
    }

    /**
     * @throws NonDeterministicException when the type may not be deterministically encoded using the
     *     given {@link Schema}, the {@code directBinaryEncoder}, and the {@link ReflectDatumWriter}
     *     or {@link GenericDatumWriter}.
     */
    @Override
    public void verifyDeterministic() throws NonDeterministicException {
        if (!nonDeterministicReasons.isEmpty()) {
            throw new NonDeterministicException(this, nonDeterministicReasons);
        }
    }

    /** Returns the schema used by this coder. */
    public Schema getSchema() {
        return schemaSupplier.get();
    }

    @Override
    public TypeDescriptor<T> getEncodedTypeDescriptor() {
        return typeDescriptor;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AlarmCoder)) {
            return false;
        }
        AlarmCoder<?> that = (AlarmCoder<?>) other;
        return Objects.equals(this.schemaSupplier.get(), that.schemaSupplier.get())
                && Objects.equals(this.typeDescriptor, that.typeDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaSupplier.get(), typeDescriptor);
    }

    /**
     * Helper class encapsulating the various pieces of state maintained by the recursive walk used
     * for checking if the encoding will be deterministic.
     */
    private static class AlarmDeterminismChecker {

        // Reasons that the original type are not deterministic. This accumulates
        // the actual output.
        private List<String> reasons = new ArrayList<>();

        // Types that are currently "open". Used to make sure we don't have any
        // recursive types. Note that we assume that all occurrences of a given type
        // are equal, rather than tracking pairs of type + schema.
        private Set<TypeDescriptor<?>> activeTypes = new HashSet<>();

        // Similarly to how we record active types, we record the schemas we visit
        // to make sure we don't encounter recursive fields.
        private Set<Schema> activeSchemas = new HashSet<>();

        /** Report an error in the current context. */
        private void reportError(String context, String fmt, Object... args) {
            String message = String.format(fmt, args);
            reasons.add(context + ": " + message);
        }

        /**
         * Classes that are serialized by Avro as a String include
         *
         * <ul>
         *   <li>Subtypes of CharSequence (including String, Avro's mutable Utf8, etc.)
         *   <li>Several predefined classes (BigDecimal, BigInteger, URI, URL)
         *   <li>Classes annotated with @Stringable (uses their #toString() and a String constructor)
         * </ul>
         *
         * <p>Rather than determine which of these cases are deterministic, we list some classes that
         * definitely are, and treat any others as non-deterministic.
         */
        private static final Set<Class<?>> DETERMINISTIC_STRINGABLE_CLASSES = new HashSet<>();

        static {
            // CharSequences:
            DETERMINISTIC_STRINGABLE_CLASSES.add(String.class);
            DETERMINISTIC_STRINGABLE_CLASSES.add(Utf8.class);

            // Explicitly Stringable:
            DETERMINISTIC_STRINGABLE_CLASSES.add(java.math.BigDecimal.class);
            DETERMINISTIC_STRINGABLE_CLASSES.add(java.math.BigInteger.class);
            DETERMINISTIC_STRINGABLE_CLASSES.add(java.net.URI.class);
            DETERMINISTIC_STRINGABLE_CLASSES.add(java.net.URL.class);

            // Classes annotated with @Stringable:
        }

        /** Return true if the given type token is a subtype of *any* of the listed parents. */
        private static boolean isSubtypeOf(TypeDescriptor<?> type, Class<?>... parents) {
            for (Class<?> parent : parents) {
                if (type.isSubtypeOf(TypeDescriptor.of(parent))) {
                    return true;
                }
            }
            return false;
        }

        protected AlarmDeterminismChecker() {}

        // The entry point for the check. Should not be recursively called.
        public List<String> check(TypeDescriptor<?> type, Schema schema) {
            recurse(type.getRawType().getName(), type, schema);
            return reasons;
        }

        // This is the method that should be recursively called. It sets up the path
        // and visited types correctly.
        private void recurse(String context, TypeDescriptor<?> type, Schema schema) {
            if (type.getRawType().isAnnotationPresent(AvroSchema.class)) {
                reportError(context, "Custom schemas are not supported -- remove @AvroSchema.");
                return;
            }

            if (!activeTypes.add(type)) {
                reportError(context, "%s appears recursively", type);
                return;
            }

            // If the the record isn't a true class, but rather a GenericRecord, SpecificRecord, etc.
            // with a specified schema, then we need to make the decision based on the generated
            // implementations.
            if (isSubtypeOf(type, IndexedRecord.class)) {
                checkIndexedRecord(context, schema, null);
            } else {
                doCheck(context, type, schema);
            }

            activeTypes.remove(type);
        }

        private void doCheck(String context, TypeDescriptor<?> type, Schema schema) {
            switch (schema.getType()) {
                case ARRAY:
                    checkArray(context, type, schema);
                    break;
                case ENUM:
                    // Enums should be deterministic, since they depend only on the ordinal.
                    break;
                case FIXED:
                    // Depending on the implementation of GenericFixed, we don't know how
                    // the given field will be encoded. So, we assume that it isn't
                    // deterministic.
                    reportError(context, "FIXED encodings are not guaranteed to be deterministic");
                    break;
                case MAP:
                    checkMap(context, type, schema);
                    break;
                case RECORD:
                    if (!(type.getType() instanceof Class)) {
                        reportError(context, "Cannot determine type from generic %s due to erasure", type);
                        return;
                    }
                    checkRecord(type, schema);
                    break;
                case UNION:
                    checkUnion(context, type, schema);
                    break;
                case STRING:
                    checkString(context, type);
                    break;
                case BOOLEAN:
                case BYTES:
                case DOUBLE:
                case INT:
                case FLOAT:
                case LONG:
                case NULL:
                    // For types that Avro encodes using one of the above primitives, we assume they are
                    // deterministic.
                    break;
                default:
                    // In any other case (eg., new types added to Avro) we cautiously return
                    // false.
                    reportError(context, "Unknown schema type %s may be non-deterministic", schema.getType());
                    break;
            }
        }

        private void checkString(String context, TypeDescriptor<?> type) {
            // For types that are encoded as strings, we need to make sure they're in an approved
            // whitelist. For other types that are annotated @Stringable, Avro will just use the
            // #toString() methods, which has no guarantees of determinism.
            if (!DETERMINISTIC_STRINGABLE_CLASSES.contains(type.getRawType())) {
                reportError(context, "%s may not have deterministic #toString()", type);
            }
        }

        private static final Schema AVRO_NULL_SCHEMA = Schema.create(Schema.Type.NULL);

        private void checkUnion(String context, TypeDescriptor<?> type, Schema schema) {
            final List<Schema> unionTypes = schema.getTypes();

            if (!type.getRawType().isAnnotationPresent(Union.class)) {
                // First check for @Nullable field, which shows up as a union of field type and null.
                if (unionTypes.size() == 2 && unionTypes.contains(AVRO_NULL_SCHEMA)) {
                    // Find the Schema that is not NULL and recursively check that it is deterministic.
                    Schema nullableFieldSchema =
                            unionTypes.get(0).equals(AVRO_NULL_SCHEMA) ? unionTypes.get(1) : unionTypes.get(0);
                    doCheck(context, type, nullableFieldSchema);
                    return;
                }

                // Otherwise report a schema error.
                reportError(context, "Expected type %s to have @Union annotation", type);
                return;
            }

            // Errors associated with this union will use the base class as their context.
            String baseClassContext = type.getRawType().getName();

            // For a union, we need to make sure that each possible instantiation is deterministic.
            for (Schema concrete : unionTypes) {
                @SuppressWarnings("unchecked")
                TypeDescriptor<?> unionType = TypeDescriptor.of(ReflectData.get().getClass(concrete));

                recurse(baseClassContext, unionType, concrete);
            }
        }

        private void checkRecord(TypeDescriptor<?> type, Schema schema) {
            // For a record, we want to make sure that all the fields are deterministic.
            Class<?> clazz = type.getRawType();
            for (Schema.Field fieldSchema : schema.getFields()) {
                Field field = getField(clazz, fieldSchema.name());
                String fieldContext = field.getDeclaringClass().getName() + "#" + field.getName();

                if (field.isAnnotationPresent(AvroEncode.class)) {
                    reportError(
                            fieldContext, "Custom encoders may be non-deterministic -- remove @AvroEncode");
                    continue;
                }

                if (!IndexedRecord.class.isAssignableFrom(field.getType())
                        && field.isAnnotationPresent(AvroSchema.class)) {
                    // TODO: We should be able to support custom schemas on POJO fields, but we shouldn't
                    // need to, so we just allow it in the case of IndexedRecords.
                    reportError(
                            fieldContext, "Custom schemas are only supported for subtypes of IndexedRecord.");
                    continue;
                }

                TypeDescriptor<?> fieldType = type.resolveType(field.getGenericType());
                recurse(fieldContext, fieldType, fieldSchema.schema());
            }
        }

        private void checkIndexedRecord(
                String context, Schema schema, @javax.annotation.Nullable String specificClassStr) {

            if (!activeSchemas.add(schema)) {
                reportError(context, "%s appears recursively", schema.getName());
                return;
            }

            switch (schema.getType()) {
                case ARRAY:
                    // Generic Records use GenericData.Array to implement arrays, which is
                    // essentially an ArrayList, and therefore ordering is deterministic.
                    // The array is thus deterministic if the elements are deterministic.
                    checkIndexedRecord(context, schema.getElementType(), null);
                    break;
                case ENUM:
                    // Enums are deterministic because they encode as a single integer.
                    break;
                case FIXED:
                    // In the case of GenericRecords, FIXED is deterministic because it
                    // encodes/decodes as a Byte[].
                    break;
                case MAP:
                    reportError(
                            context,
                            "GenericRecord and SpecificRecords use a HashMap to represent MAPs,"
                                    + " so it is non-deterministic");
                    break;
                case RECORD:
                    for (Schema.Field field : schema.getFields()) {
                        checkIndexedRecord(
                                schema.getName() + "." + field.name(),
                                field.schema(),
                                field.getProp(SpecificData.CLASS_PROP));
                    }
                    break;
                case STRING:
                    // GenericDatumWriter#findStringClass will use a CharSequence or a String
                    // for each string, so it is deterministic.

                    // SpecificCompiler#getStringType will use java.lang.String, org.apache.avro.util.Utf8,
                    // or java.lang.CharSequence, unless SpecificData.CLASS_PROP overrides that.
                    if (specificClassStr != null) {
                        Class<?> specificClass;
                        try {
                            specificClass = ClassUtils.forName(specificClassStr);
                            if (!DETERMINISTIC_STRINGABLE_CLASSES.contains(specificClass)) {
                                reportError(
                                        context,
                                        "Specific class %s is not known to be deterministic",
                                        specificClassStr);
                            }
                        } catch (ClassNotFoundException e) {
                            reportError(
                                    context, "Specific class %s is not known to be deterministic", specificClassStr);
                        }
                    }
                    break;
                case UNION:
                    for (Schema subschema : schema.getTypes()) {
                        checkIndexedRecord(subschema.getName(), subschema, null);
                    }
                    break;
                case BOOLEAN:
                case BYTES:
                case DOUBLE:
                case INT:
                case FLOAT:
                case LONG:
                case NULL:
                    // For types that Avro encodes using one of the above primitives, we assume they are
                    // deterministic.
                    break;
                default:
                    reportError(context, "Unknown schema type %s may be non-deterministic", schema.getType());
                    break;
            }

            activeSchemas.remove(schema);
        }

        private void checkMap(String context, TypeDescriptor<?> type, Schema schema) {
            if (!isSubtypeOf(type, SortedMap.class)) {
                reportError(context, "%s may not be deterministically ordered", type);
            }

            // Avro (currently) asserts that all keys are strings.
            // In case that changes, we double check that the key was a string:
            Class<?> keyType = type.resolveType(Map.class.getTypeParameters()[0]).getRawType();
            if (!String.class.equals(keyType)) {
                reportError(context, "map keys should be Strings, but was %s", keyType);
            }

            recurse(context, type.resolveType(Map.class.getTypeParameters()[1]), schema.getValueType());
        }

        private void checkArray(String context, TypeDescriptor<?> type, Schema schema) {
            TypeDescriptor<?> elementType = null;
            if (type.isArray()) {
                // The type is an array (with ordering)-> deterministic iff the element is deterministic.
                elementType = type.getComponentType();
            } else if (isSubtypeOf(type, Collection.class)) {
                if (isSubtypeOf(type, List.class, SortedSet.class)) {
                    // Ordered collection -> deterministic iff the element is deterministic
                    elementType = type.resolveType(Collection.class.getTypeParameters()[0]);
                } else {
                    // Not an ordered collection -> not deterministic
                    reportError(context, "%s may not be deterministically ordered", type);
                    return;
                }
            } else {
                // If it was an unknown type encoded as an array, be conservative and assume
                // that we don't know anything about the order.
                reportError(context, "encoding %s as an ARRAY was unexpected", type);
                return;
            }

            // If we get here, it's either a deterministically-ordered Collection, or
            // an array. Either way, the type is deterministic iff the element type is
            // deterministic.
            recurse(context, elementType, schema.getElementType());
        }

        /**
         * Extract a field from a class. We need to look at the declared fields so that we can see
         * private fields. We may need to walk up to the parent to get classes from the parent.
         */
        private static Field getField(Class<?> originalClazz, String name) {
            Class<?> clazz = originalClazz;
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    AvroName avroName = field.getAnnotation(AvroName.class);
                    if (avroName != null && name.equals(avroName.value())) {
                        return field;
                    } else if (avroName == null && name.equals(field.getName())) {
                        return field;
                    }
                }
                clazz = clazz.getSuperclass();
            }

            throw new IllegalArgumentException("Unable to get field " + name + " from " + originalClazz);
        }
    }


}
